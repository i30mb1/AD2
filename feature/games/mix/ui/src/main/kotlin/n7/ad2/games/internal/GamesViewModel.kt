package n7.ad2.games.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.LocalDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import n7.ad2.Resources
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.WifiDirect
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.games.show.DailyResult
import n7.ad2.games.internal.games.show.DailyStore
import n7.ad2.games.internal.party.NearbyPlayer
import n7.ad2.games.internal.party.Party
import n7.ad2.games.internal.party.PartyState
import n7.ad2.games.internal.progress.Progress
import n7.ad2.games.internal.progress.ProgressStore
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.LocalPlayerProvider
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.RoomFactory

/**
 * Список игр и игра по сети: пока список на экране, телефон виден рядом и видит других ([Party]).
 * Комната с другими игроками переживает игры: после игры все возвращаются сюда, хост выбирает следующую.
 */
internal class GamesViewModel @AssistedInject constructor(
    res: Resources,
    leaderboardRepository: LeaderboardRepository,
    localPlayerProvider: LocalPlayerProvider,
    roomFactory: RoomFactory,
    leaderboardSync: LeaderboardSync,
    getNetworkStateUseCase: GetNetworkStateUseCase,
    discoverServicesInNetworkUseCase: DiscoverServicesInNetworkUseCase,
    registerServiceInNetworkUseCase: RegisterServiceInNetworkUseCase,
    wifiDirect: WifiDirect,
    private val dailyStore: DailyStore,
    private val progressStore: ProgressStore,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): GamesViewModel
    }

    private val me = localPlayerProvider.localPlayer()

    private val games = listOf(GameVO.Show(res), GameVO.Mid(res), GameVO.Invoker(res), GameVO.XO(res))

    private val party = Party(
        me,
        viewModelScope,
        roomFactory,
        leaderboardSync,
        getNetworkStateUseCase,
        discoverServicesInNetworkUseCase,
        registerServiceInNetworkUseCase,
        wifiDirect,
    )

    // Channel, а не SharedFlow: «Ещё раз» приходит в onStart, раньше чем фрагмент начнет собирать события
    private val _openGame = Channel<OpenGame>(Channel.BUFFERED)

    /** Открыть экран игры: по сети - когда комната перешла в Playing, соло - «Ещё раз» */
    val openGame: Flow<OpenGame> = _openGame.receiveAsFlow()

    // личный соло-рекорд для значка на карточке каждой игры
    private val bests = combine(
        games.map { game ->
            leaderboardRepository.observe(game.id, GameMode.SOLO)
                .map { records -> game.id to records.firstOrNull { it.playerId == me.id }?.score }
                .catch { emit(game.id to null) }
        },
    ) { bests -> bests.mapNotNull { (id, score) -> score?.let { id to it } }.toMap() }

    // вызов дня: сыгран ли сегодня и серия; обновляется, когда список снова на экране
    private val daily = MutableStateFlow(dailyCard())

    // уровень игрока растет после игр: обновляется, когда список снова на экране
    private val progress = MutableStateFlow(progressStore.progress())

    // список статичный: показываем сразу, рекорды и игроки рядом подтянутся
    val state: StateFlow<State> = combine(bests, party.state, daily, progress) { bests, party, daily, progress -> State.Data(games, bests, party, daily, progress) }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.Data(games, emptyMap(), party.state.value, daily.value, progress.value))

    init {
        viewModelScope.launch {
            party.openGame.collect { playing -> _openGame.send(OpenGame(playing.gameId, playing.mode)) }
        }
    }

    /** Список на экране: объявляемся и ищем игроков; вернулись с «Ещё раз» - выбираем ту же игру */
    fun setVisible(visible: Boolean) {
        party.setVisible(visible)
        if (!visible) return
        daily.value = dailyCard()
        progress.value = progressStore.progress()
        val rematch = ActiveRoom.takeRematch() ?: return
        if (rematch.mode == GameMode.SOLO) _openGame.trySend(OpenGame(rematch.gameId, GameMode.SOLO)) else party.rematch(rematch)
    }

    fun onWifiDirectPermission(granted: Boolean) = party.onWifiDirectPermission(granted)

    fun play(player: NearbyPlayer) = party.play(player)

    /** @param address "ip:port" */
    fun joinManual(address: String) = party.joinManual(address)

    /** Хост выбирает игру комнаты */
    fun pick(gameId: String, mode: GameMode) = party.pick(gameId, mode)

    /** Гость готов или нет к выбранной игре */
    fun setReady(ready: Boolean) = party.setReady(ready)

    /** Хост запускает игру, когда готовы все */
    fun start() = party.start()

    fun leave() = party.leave()

    override fun onCleared() = party.release()

    private fun dailyCard(): DailyCard {
        val today = LocalDate.now()
        return DailyCard(today, dailyStore.result(today), dailyStore.streak().current(today))
    }

    data class OpenGame(val gameId: String, val mode: GameMode)

    /** [result] null - today's challenge is not played yet */
    data class DailyCard(val date: LocalDate, val result: DailyResult?, val streak: Int)

    sealed class State {
        data class Data(val games: List<GameVO>, val bests: Map<String, Int>, val party: PartyState, val daily: DailyCard, val progress: Progress) : State()
    }
}

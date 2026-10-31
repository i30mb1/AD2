package n7.ad2.games.internal.games.skillmp

import android.graphics.Color
import android.os.SystemClock
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.games.internal.ActiveRoom
import n7.ad2.games.internal.MatchClock
import n7.ad2.games.internal.party.GameChannel
import n7.ad2.games.internal.party.Pick
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.LocalPlayerProvider
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomState

internal class SkillGameViewModel @AssistedInject constructor(
    @Assisted private val mode: GameMode,
    @Assisted private val gameId: String,
    private val getSkillsUseCase: GetSkillsUseCase,
    private val getHeroQuizUseCase: GetHeroQuizUseCase,
    private val getCompareQuizUseCase: GetCompareQuizUseCase,
    private val getBuildQuizUseCase: GetBuildQuizUseCase,
    private val leaderboardSync: LeaderboardSync,
    private val leaderboardRepository: LeaderboardRepository,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(mode: GameMode, gameId: String): SkillGameViewModel
    }

    private val room: Room? = ActiveRoom.room.takeIf { mode != GameMode.SOLO }
    private val playing = room?.state?.value as? RoomState.Playing
    private val seed: Long? = playing?.seed
    private val me = room?.me ?: localPlayerProvider.localPlayer()
    private val peerName = playing?.let { if (it.host.id == me.id) it.guest.name else it.host.name }

    private val _state = MutableStateFlow(State.init(SpellMatch.start(mode), peerName))
    val state: StateFlow<State> = _state.asStateFlow()

    /** Экран итогов закрыт («Ещё раз» или «Другая игра»): вернуться к списку игр */
    private val _exit = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val exit: SharedFlow<Unit> = _exit.asSharedFlow()

    private var totalAnswerMs = 0L

    private val pendingPeerAnswers = mutableMapOf<Int, Answer>()
    private var questionShownAt = 0L
    private var advancingRound = -1
    private var questionJob: Job? = null

    init {
        if (room != null) {
            observeRoom(room)
            viewModelScope.launch { countdownThenStart() }
        } else {
            loadQuestion()
        }
    }

    /** Та же игра и режим: вдвоем список игр выберет ее снова, соло откроет заново */
    fun again() {
        ActiveRoom.requestRematch(Pick(gameId, mode))
        _exit.tryEmit(Unit)
    }

    fun otherGame() {
        _exit.tryEmit(Unit)
    }

    // общий старт: момент перехода комнаты в Playing у обоих почти одновременно, экраны открываются в разное время
    private suspend fun countdownThenStart() {
        val clock = MatchClock(ActiveRoom.startedAt, COUNTDOWN_MS)
        while (true) {
            val left = clock.countdown(SystemClock.elapsedRealtime())
            _state.update { it.copy(countdown = left) }
            if (left == 0) break
            delay(100)
        }
        loadQuestion()
    }

    fun onAnswer(spell: Spell) {
        if (canAnswer()) {
            _state.update { it.copy(selectedSpell = spell) }
            submit(spell.isRightAnswer)
        }
    }

    /** «Собери предмет»: тап кладет плитку в слот, повторный - убирает; сверх числа слотов выбрать нельзя */
    fun onToggleTile(index: Int) {
        val build = _state.value.build ?: return
        if (!canAnswer()) return
        _state.update { state ->
            val selected = state.selectedTiles
            when {
                index in selected -> state.copy(selectedTiles = selected - index)
                selected.size < build.slots -> state.copy(selectedTiles = selected + index)
                else -> state
            }
        }
    }

    fun onBuild() {
        val state = _state.value
        val build = state.build ?: return
        if (!canAnswer() || state.selectedTiles.size != build.slots) return
        submit(state.selectedTiles.all { build.tiles[it].isCorrect })
    }

    private fun canAnswer(): Boolean = _state.value.let { !it.isLoading && !it.showRightAnswer && !it.match.isOver }

    private fun submit(correct: Boolean) {
        val round = _state.value.match.round
        val answer = Answer(correct, SystemClock.elapsedRealtime() - questionShownAt)
        totalAnswerMs += answer.timeMs
        _state.update { state ->
            val streak = if (correct) state.streak + 1 else 0
            val answered = state.answered + 1
            state.copy(
                showRightAnswer = true,
                match = state.match.answer(mine = true, answer),
                streak = streak,
                bestStreak = maxOf(state.bestStreak, streak),
                correct = state.correct + if (correct) 1 else 0,
                answered = answered,
                avgAnswerMs = totalAnswerMs / answered,
            )
        }
        room?.let { runCatching { it.sendEvent(PeerAnswerCodec.encode(round, answer)) } }
        advanceIfRoundComplete()
    }

    private fun observeRoom(room: Room) {
        viewModelScope.launch {
            room.events.collect { event ->
                val (round, answer) = PeerAnswerCodec.decode(event.payload) ?: return@collect
                if (round == _state.value.match.round) {
                    _state.update { it.copy(match = it.match.answer(mine = false, answer)) }
                    advanceIfRoundComplete()
                } else if (round > _state.value.match.round) {
                    pendingPeerAnswers[round] = answer
                }
            }
        }
        viewModelScope.launch {
            room.state.first { it is RoomState.Closed }
            onPeerLeft()
        }
        viewModelScope.launch {
            room.syncEvents.first(GameChannel::isQuit)
            onPeerLeft()
        }
    }

    private fun onPeerLeft() {
        if (!_state.value.match.isOver) {
            _state.update { it.copy(match = it.match.peerLeft()) }
            finish()
        }
    }

    private fun advanceIfRoundComplete() {
        val match = _state.value.match
        if (!match.roundComplete || advancingRound == match.round) return
        advancingRound = match.round
        viewModelScope.launch {
            delay(1.seconds)
            if (_state.value.match.isOver) {
                finish()
            } else {
                _state.update { it.copy(match = it.match.nextRound()) }
                loadQuestion()
            }
        }
    }

    private fun loadQuestion() {
        questionJob?.cancel()
        val round = _state.value.match.round
        questionJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, showRightAnswer = false, selectedSpell = null, selectedTiles = emptySet()) }
            delay(0.2.seconds)
            val data = loadData(round) ?: return@launch _state.update { it.copy(isLoading = false, isError = true) }
            _state.update { state ->
                state.copy(
                    isLoading = false,
                    backgroundColor = data.backgroundColor,
                    spellImage = data.skillImage,
                    spellList = SpellList(data.suggestsSpellList.map { Spell(it.cost, it.isRightAnswer) }),
                    spellLVL = data.spellLVL,
                    compare = data.compare,
                    build = data.build,
                )
            }
            questionShownAt = SystemClock.elapsedRealtime()
            pendingPeerAnswers.remove(round)?.let { answer ->
                _state.update { it.copy(match = it.match.answer(mine = false, answer)) }
                advanceIfRoundComplete()
            }
        }
    }

    /**
     * В сетевой игре попытка k берет Random(seed, раунд, k): если у выпавшего героя нет подходящего спелла,
     * оба телефона одинаково переходят к следующей попытке и получают один и тот же вопрос
     */
    private suspend fun loadData(round: Int): GetSkillsUseCase.Data? {
        repeat(MAX_ATTEMPTS) { attempt ->
            val random = seed?.let { Random(it + round * MAX_ATTEMPTS + attempt) } ?: Random.Default
            runCatching {
                when (gameId) {
                    HERO_GAME_ID -> getHeroQuizUseCase(random).first()
                    COMPARE_GAME_ID -> getCompareQuizUseCase(random).first()
                    BUILD_GAME_ID -> GetSkillsUseCase.Data("", "", "", Color.TRANSPARENT, emptyList(), 0, build = getBuildQuizUseCase(random))
                    else -> getSkillsUseCase(random).first()
                }
            }.onSuccess { return it }
        }
        return null
    }

    private fun finish() {
        if (_state.value.isEndGame) return
        _state.update { it.copy(isEndGame = true) }
        val score = _state.value.match.myScore
        // комната остается для следующей игры
        room?.endGame()
        viewModelScope.launch {
            val previousBest = runCatching { leaderboardRepository.observe(gameId, mode).first().firstOrNull { it.playerId == me.id }?.score }.getOrNull()
            val isRecord = leaderboardSync.submit(Record(me.id, me.name, gameId, mode, score, System.currentTimeMillis()))
            _state.update { it.copy(newRecord = isRecord && score > 0, previousBest = previousBest) }
        }
    }

    override fun onCleared() {
        val room = room ?: return
        if (!_state.value.isEndGame) GameChannel.quit(room)
        room.endGame()
    }

    @Immutable
    internal data class State(
        val isLoading: Boolean,
        val isError: Boolean,
        val backgroundColor: Int,
        val spellImage: String,
        val spellList: SpellList,
        val spellLVL: Int,
        val showRightAnswer: Boolean,
        val selectedSpell: Spell?,
        val match: SpellMatch,
        val compare: GetSkillsUseCase.Compare?,
        val build: BuildQuestion?,
        val selectedTiles: Set<Int>,
        val peerName: String?,
        val isEndGame: Boolean,
        val newRecord: Boolean,
        /** Лучший результат до этой игры, null - первая игра */
        val previousBest: Int?,
        /** Секунды общего отсчета перед первым вопросом, 0 - игра идет */
        val countdown: Int,
        val streak: Int,
        val bestStreak: Int,
        val correct: Int,
        val answered: Int,
        val avgAnswerMs: Long?,
    ) {
        companion object {
            fun init(match: SpellMatch, peerName: String?) = State(
                isLoading = true,
                isError = false,
                backgroundColor = Color.TRANSPARENT,
                spellImage = "",
                spellList = SpellList(emptyList()),
                spellLVL = 0,
                showRightAnswer = false,
                selectedSpell = null,
                match = match,
                compare = null,
                build = null,
                selectedTiles = emptySet(),
                peerName = peerName,
                isEndGame = false,
                newRecord = false,
                previousBest = null,
                countdown = 0,
                streak = 0,
                bestStreak = 0,
                correct = 0,
                answered = 0,
                avgAnswerMs = null,
            )
        }
    }

    @Immutable
    internal data class SpellList(val list: List<Spell>)

    @Immutable
    internal data class Spell(val cost: String, val isRightAnswer: Boolean)

    companion object {
        const val GAME_ID = "spell"
        const val HERO_GAME_ID = "hero"
        const val COMPARE_GAME_ID = "compare"
        const val BUILD_GAME_ID = "build"
        private const val MAX_ATTEMPTS = 20
        private const val COUNTDOWN_MS = 3_000L
    }
}

/** Ответ соперника в формате "раунд;верно;мс" */
internal object PeerAnswerCodec {
    fun encode(round: Int, answer: Answer): String = "$round;${answer.correct};${answer.timeMs}"

    fun decode(payload: String): Pair<Int, Answer>? {
        val parts = payload.split(";")
        if (parts.size != 3) return null
        val round = parts[0].toIntOrNull() ?: return null
        val correct = parts[1].toBooleanStrictOrNull() ?: return null
        val time = parts[2].toLongOrNull() ?: return null
        return round to Answer(correct, time)
    }
}

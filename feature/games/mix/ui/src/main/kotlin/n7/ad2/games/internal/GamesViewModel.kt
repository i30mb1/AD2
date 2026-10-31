package n7.ad2.games.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import n7.ad2.Resources
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.LocalPlayerProvider
import n7.ad2.games.room.GameMode

internal class GamesViewModel @AssistedInject constructor(
    private val res: Resources,
    leaderboardRepository: LeaderboardRepository,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): GamesViewModel
    }

    private val myId = localPlayerProvider.localPlayer().id

    private val games = listOf(GameVO.GuessSkillMana(res), GameVO.GuessHero(res), GameVO.HigherLower(res), GameVO.Apm(res), GameVO.XO(res))

    // личный соло-рекорд для значка на карточке каждой викторины
    val state: StateFlow<State> = combine(
        games.mapNotNull { it.quizId }.map { quizId ->
            leaderboardRepository.observe(quizId, GameMode.SOLO)
                .map { records -> quizId to records.firstOrNull { it.playerId == myId }?.score }
                .catch { emit(quizId to null) }
        },
    ) { bests -> State.Data(games, bests.mapNotNull { (id, score) -> score?.let { id to it } }.toMap()) }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.Loading)

    sealed class State {
        data class Data(val games: List<GameVO>, val bests: Map<String, Int>) : State()
        object Loading : State()
    }
}

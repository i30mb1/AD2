package n7.ad2.games.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import n7.ad2.Resources
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel
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

    val state: StateFlow<State> = leaderboardRepository.observe(SkillGameViewModel.GAME_ID, GameMode.SOLO)
        .map { records -> records.firstOrNull { it.playerId == myId }?.score }
        .catch { emit(null) }
        .map { spellBest ->
            State.Data(
                games = listOf(GameVO.GuessSkillMana(res), GameVO.Apm(res), GameVO.XO(res)),
                spellBest = spellBest,
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.Loading)

    sealed class State {
        data class Data(val games: List<GameVO>, val spellBest: Int?) : State()
        object Loading : State()
    }
}

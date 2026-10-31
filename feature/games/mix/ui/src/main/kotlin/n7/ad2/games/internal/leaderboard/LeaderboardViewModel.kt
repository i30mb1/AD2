package n7.ad2.games.internal.leaderboard

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import n7.ad2.games.internal.ActiveRoom
import n7.ad2.games.internal.games.invoker.InvokerViewModel
import n7.ad2.games.internal.games.mid.MidViewModel
import n7.ad2.games.internal.games.show.ShowViewModel
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.LocalPlayerProvider
import n7.ad2.games.leaderboard.Record
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.RoomState

internal class LeaderboardViewModel @AssistedInject constructor(
    private val repository: LeaderboardRepository,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): LeaderboardViewModel
    }

    private val myId = localPlayerProvider.localPlayer().id
    private val filter = MutableStateFlow(ShowViewModel.GAME_ID to GameMode.SOLO)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<State> = filter
        .flatMapLatest { (game, mode) -> repository.observe(game, mode).map { State(game, mode, it, myId, onlinePlayerIds()) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), State(ShowViewModel.GAME_ID, GameMode.SOLO, emptyList(), myId, emptySet()))

    fun selectMode(selected: GameMode) {
        filter.value = filter.value.first to selected
    }

    fun selectGame(gameId: String) {
        filter.value = gameId to filter.value.second
    }

    private fun onlinePlayerIds(): Set<String> = when (val roomState = ActiveRoom.room?.state?.value) {
        is RoomState.Ready -> setOf(roomState.host.id, roomState.guest.id)
        is RoomState.Playing -> setOf(roomState.host.id, roomState.guest.id)
        else -> emptySet()
    } - myId

    @Immutable
    data class State(val gameId: String, val mode: GameMode, val records: List<Record>, val myId: String, val online: Set<String>) {
        companion object {
            val GAMES = listOf(ShowViewModel.GAME_ID, MidViewModel.GAME_ID, InvokerViewModel.GAME_ID)
        }
    }
}

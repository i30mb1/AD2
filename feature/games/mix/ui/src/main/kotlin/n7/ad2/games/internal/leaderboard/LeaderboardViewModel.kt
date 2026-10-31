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
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel
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
    private val mode = MutableStateFlow(GameMode.SOLO)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<State> = mode
        .flatMapLatest { selected -> repository.observe(SkillGameViewModel.GAME_ID, selected).map { State(selected, it, myId, onlinePlayerIds()) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), State(GameMode.SOLO, emptyList(), myId, emptySet()))

    fun selectMode(selected: GameMode) {
        mode.value = selected
    }

    private fun onlinePlayerIds(): Set<String> = when (val roomState = ActiveRoom.room?.state?.value) {
        is RoomState.Ready -> setOf(roomState.host.id, roomState.guest.id)
        is RoomState.Playing -> setOf(roomState.host.id, roomState.guest.id)
        else -> emptySet()
    } - myId

    @Immutable
    data class State(val mode: GameMode, val records: List<Record>, val myId: String, val online: Set<String>)
}

package n7.ad2.games.internal.games.invoker

import android.os.SystemClock
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
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

/**
 * Invoker: a minute to invoke as many spells as possible. Versus - the same spells on both phones, who scores more;
 * together - one team score. Each phone counts its own score and sends it after every spell.
 */
internal class InvokerViewModel @AssistedInject constructor(
    @Assisted private val mode: GameMode,
    private val leaderboardSync: LeaderboardSync,
    private val leaderboardRepository: LeaderboardRepository,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(mode: GameMode): InvokerViewModel
    }

    private val room: Room? = ActiveRoom.room.takeIf { mode != GameMode.SOLO }
    private val playing = room?.state?.value as? RoomState.Playing
    private val me = room?.me ?: localPlayerProvider.localPlayer()
    private val peerName = playing?.let { if (it.host.id == me.id) it.guest.name else it.host.name }
    private val seed = playing?.seed ?: Random.nextLong()

    /** Over the network both start at the moment the room started the game, alone - now; 3-2-1 first */
    private val clock = MatchClock(if (room != null) ActiveRoom.startedAt else SystemClock.elapsedRealtime(), COUNTDOWN_MS)

    private val _state = MutableStateFlow(State.start(mode, InvokerRun(spellQueue(seed)), peerName))
    val state: StateFlow<State> = _state.asStateFlow()

    private val _exit = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val exit: SharedFlow<Unit> = _exit.asSharedFlow()

    init {
        room?.let(::observeRoom)
        viewModelScope.launch {
            while (isActive && _state.value.phase == State.Phase.PLAYING) {
                if (elapsedMs() >= RUN_MS) finish()
                delay(TICK_MS)
            }
        }
    }

    /** Negative during the countdown */
    fun elapsedMs(): Long = clock.elapsed(SystemClock.elapsedRealtime())

    fun countdown(): Int = clock.countdown(SystemClock.elapsedRealtime())

    fun press(orb: Orb) = play { it.press(orb) }

    fun invoke() {
        val before = _state.value.run
        play { it.invoke() }
        val after = _state.value.run
        if (after.invoked > before.invoked) room?.let { runCatching { it.sendEvent(InvokerCodec.encode(after.score, after.invoked)) } }
    }

    /** Opens the recipe of the spell: it gives half the points */
    fun hint() = play { it.hint() }

    fun again() {
        ActiveRoom.requestRematch(Pick(GAME_ID, mode))
        _exit.tryEmit(Unit)
    }

    fun otherGame() {
        _exit.tryEmit(Unit)
    }

    private fun play(change: (InvokerRun) -> InvokerRun) {
        val elapsed = elapsedMs()
        if (_state.value.phase != State.Phase.PLAYING || elapsed < 0 || elapsed >= RUN_MS) return
        _state.update { it.copy(run = change(it.run)) }
    }

    private fun observeRoom(room: Room) {
        viewModelScope.launch {
            room.events.collect { event ->
                val (score, invoked) = InvokerCodec.decode(event.payload) ?: return@collect
                // messages may come out of order: the score only grows
                _state.update { if (invoked > it.peerInvoked) it.copy(peerScore = score, peerInvoked = invoked) else it }
            }
        }
        viewModelScope.launch {
            room.state.first { it is RoomState.Closed }
            peerLeft()
        }
        viewModelScope.launch {
            room.syncEvents.first(GameChannel::isQuit)
            peerLeft()
        }
    }

    private fun peerLeft() {
        if (_state.value.phase == State.Phase.END) return
        _state.update { it.copy(peerLeft = true) }
        finish()
    }

    private fun finish() {
        if (_state.value.phase == State.Phase.END) return
        _state.update { it.copy(phase = State.Phase.END) }
        // the room stays for the next game
        room?.endGame()
        val state = _state.value
        val score = if (mode == GameMode.COOP) state.run.score + state.peerScore else state.run.score
        viewModelScope.launch {
            val previousBest = runCatching { leaderboardRepository.observe(GAME_ID, mode).first().firstOrNull { it.playerId == me.id }?.score }.getOrNull()
            val isRecord = leaderboardSync.submit(Record(me.id, me.name, GAME_ID, mode, score, System.currentTimeMillis()))
            _state.update { it.copy(newRecord = isRecord && score > 0, previousBest = previousBest) }
        }
    }

    override fun onCleared() {
        val room = room ?: return
        if (_state.value.phase != State.Phase.END) GameChannel.quit(room)
        room.endGame()
    }

    @Immutable
    internal data class State(
        val mode: GameMode,
        val phase: Phase,
        val run: InvokerRun,
        val peerName: String?,
        val peerScore: Int,
        val peerInvoked: Int,
        val peerLeft: Boolean,
        val newRecord: Boolean,
        /** The best result before this run, null - the first one */
        val previousBest: Int?,
    ) {
        enum class Phase { PLAYING, END }

        companion object {
            fun start(mode: GameMode, run: InvokerRun, peerName: String?) = State(
                mode = mode,
                phase = Phase.PLAYING,
                run = run,
                peerName = peerName,
                peerScore = 0,
                peerInvoked = 0,
                peerLeft = false,
                newRecord = false,
                previousBest = null,
            )
        }
    }

    companion object {
        const val GAME_ID = "invoker"
        const val RUN_MS = 60_000L
        private const val COUNTDOWN_MS = 3_000L
        private const val TICK_MS = 100L
    }
}

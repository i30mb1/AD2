package n7.ad2.games.internal.games.xo

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.withContext
import n7.ad2.games.internal.ActiveRoom
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
 * Tic-tac-toe 9×9. Solo - against the bot, versus - against a friend over the room: each phone plays the moves
 * by the same rules, sends its own and checks the other one's. The record is wins in a row.
 */
internal class XoViewModel @AssistedInject constructor(
    @Assisted private val mode: GameMode,
    private val leaderboardSync: LeaderboardSync,
    private val leaderboardRepository: LeaderboardRepository,
    private val store: XoStore,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(mode: GameMode): XoViewModel
    }

    private val room: Room? = ActiveRoom.room.takeIf { mode != GameMode.SOLO }
    private val playing = room?.state?.value as? RoomState.Playing
    private val me = room?.me ?: localPlayerProvider.localPlayer()
    private val isHost = playing == null || playing.host.id == me.id
    private val seed = playing?.seed ?: Random.nextLong()
    private val botRandom = Random(seed)

    /** The seed picks who plays Radiant and starts, the same on both phones: an even one gives X to the host */
    private val myMark = if (((seed and 1L) == 0L) == isHost) Mark.X else Mark.O

    private val _state = MutableStateFlow(
        State(
            mode = mode,
            game = XoGame(),
            myMark = myMark,
            peerName = playing?.let { if (isHost) it.guest.name else it.host.name },
            peerReady = room == null,
            streak = store.streak(mode).current,
        ),
    )
    val state: StateFlow<State> = _state.asStateFlow()

    /** The result screen is closed («Again» or «Another game»): back to the games list */
    private val _exit = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val exit: SharedFlow<Unit> = _exit.asSharedFlow()

    private var resignJob: Job? = null
    private var declinedJob: Job? = null

    init {
        val room = room
        if (room != null) {
            observeRoom(room)
            send(XoEvent.Hello)
        } else {
            botTurn()
        }
    }

    fun play(move: XoMove) {
        val state = _state.value
        if (!state.myTurn || !state.game.canPlay(move)) return
        val number = state.game.moveCount
        // a move instead of an answer declines the draw offered to me
        _state.update { it.copy(game = it.game.play(move), drawOffer = it.drawOffer.takeUnless { offer -> offer == DrawOffer.RECEIVED }, confirmResign = false) }
        send(XoEvent.Move(move, number))
        afterMove()
        botTurn()
    }

    fun offerDraw() {
        val state = _state.value
        if (room == null || state.result != null || !state.peerReady || state.drawOffer != null) return
        _state.update { it.copy(drawOffer = DrawOffer.SENT) }
        send(XoEvent.OfferDraw)
    }

    fun acceptDraw() {
        if (_state.value.drawOffer != DrawOffer.RECEIVED) return
        send(XoEvent.AcceptDraw)
        finish(XoResult(null, XoResult.Reason.AGREED))
    }

    fun declineDraw() {
        if (_state.value.drawOffer != DrawOffer.RECEIVED) return
        _state.update { it.copy(drawOffer = null) }
        send(XoEvent.DeclineDraw)
    }

    /** The first tap asks for one more, the second one gives the game up */
    fun resign() {
        val state = _state.value
        if (state.result != null) return
        if (!state.confirmResign) {
            _state.update { it.copy(confirmResign = true) }
            resignJob?.cancel()
            resignJob = viewModelScope.launch {
                delay(RESIGN_CONFIRM_MS)
                _state.update { it.copy(confirmResign = false) }
            }
            return
        }
        send(XoEvent.Resign)
        finish(XoResult(myMark.other, XoResult.Reason.RESIGN))
    }

    fun again() {
        ActiveRoom.requestRematch(Pick(GAME_ID, mode))
        _exit.tryEmit(Unit)
    }

    fun otherGame() {
        _exit.tryEmit(Unit)
    }

    private fun botTurn() {
        val game = _state.value.game
        if (room != null || _state.value.result != null || game.turn == myMark) return
        viewModelScope.launch {
            delay(BOT_THINK_MS + botRandom.nextLong(BOT_THINK_SPREAD_MS))
            val move = withContext(Dispatchers.Default) { botMove(game, botRandom) }
            // the player could resign while the bot was thinking
            if (_state.value.result != null) return@launch
            _state.update { it.copy(game = it.game.play(move)) }
            afterMove()
        }
    }

    private fun afterMove() {
        _state.value.game.result?.let(::finish)
    }

    private fun observeRoom(room: Room) {
        viewModelScope.launch {
            room.events.collect { event -> XoCodec.decode(event.payload)?.let(::onPeer) }
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

    private fun onPeer(event: XoEvent) {
        val state = _state.value
        when (event) {
            XoEvent.Hello -> {
                send(XoEvent.HelloBack)
                _state.update { it.copy(peerReady = true) }
            }
            XoEvent.HelloBack -> _state.update { it.copy(peerReady = true) }
            is XoEvent.Move -> {
                val game = state.game
                if (state.result != null || game.turn == myMark || event.number != game.moveCount || !game.canPlay(event.move)) return
                // the other one moved instead of answering my offer: declined
                _state.update { it.copy(game = it.game.play(event.move), drawOffer = it.drawOffer.takeUnless { offer -> offer == DrawOffer.SENT }) }
                afterMove()
            }
            XoEvent.Resign -> finish(XoResult(myMark, XoResult.Reason.RESIGN))
            // both offered at once: both want a draw
            XoEvent.OfferDraw -> if (state.drawOffer == DrawOffer.SENT) {
                finish(XoResult(null, XoResult.Reason.AGREED))
            } else if (state.result == null) {
                _state.update { it.copy(drawOffer = DrawOffer.RECEIVED) }
            }
            XoEvent.AcceptDraw -> finish(XoResult(null, XoResult.Reason.AGREED))
            XoEvent.DeclineDraw -> if (state.drawOffer == DrawOffer.SENT) {
                _state.update { it.copy(drawOffer = DrawOffer.DECLINED) }
                declinedJob?.cancel()
                declinedJob = viewModelScope.launch {
                    delay(DECLINED_SHOWN_MS)
                    _state.update { if (it.drawOffer == DrawOffer.DECLINED) it.copy(drawOffer = null) else it }
                }
            }
        }
    }

    private fun peerLeft() = finish(XoResult(myMark, XoResult.Reason.LEFT))

    private fun finish(result: XoResult) {
        if (_state.value.result != null) return
        resignJob?.cancel()
        // the room stays for the next game
        room?.endGame()
        val streak = store.streak(mode).let { if (result.winner == myMark) it.won() else it.lost() }
        store.save(mode, streak)
        _state.update { it.copy(result = result, drawOffer = null, confirmResign = false, streak = streak.current) }
        if (streak.current == 0) return
        viewModelScope.launch {
            val previousBest = runCatching { leaderboardRepository.observe(GAME_ID, mode).first().firstOrNull { it.playerId == me.id }?.score }.getOrNull()
            val isRecord = leaderboardSync.submit(Record(me.id, me.name, GAME_ID, mode, streak.current, System.currentTimeMillis()))
            _state.update { it.copy(newRecord = isRecord, previousBest = previousBest) }
        }
    }

    private fun send(event: XoEvent) {
        val room = room ?: return
        runCatching { room.sendEvent(XoCodec.encode(event)) }
    }

    override fun onCleared() {
        val state = _state.value
        // leaving a started game is a loss for the streak
        if (state.result == null && state.game.moveCount > 0) store.save(mode, store.streak(mode).lost())
        val room = room ?: return
        if (state.result == null) GameChannel.quit(room)
        room.endGame()
    }

    /** [SENT] - I offered and wait, [RECEIVED] - the other one offers, [DECLINED] - my offer was declined, shown for a moment */
    enum class DrawOffer { SENT, RECEIVED, DECLINED }

    /**
     * @param peerName null - the bot
     * @param peerReady over the network moves wait until the other phone has the game open
     * @param streak wins in a row with this one
     */
    @Immutable
    internal data class State(
        val mode: GameMode,
        val game: XoGame,
        val myMark: Mark,
        val peerName: String?,
        val peerReady: Boolean,
        val result: XoResult? = null,
        val drawOffer: DrawOffer? = null,
        val confirmResign: Boolean = false,
        val streak: Int = 0,
        val newRecord: Boolean = false,
        /** The best streak before this game, null - the first one */
        val previousBest: Int? = null,
    ) {
        val myTurn: Boolean get() = result == null && peerReady && game.turn == myMark
    }

    companion object {
        const val GAME_ID = "xo"
        private const val BOT_THINK_MS = 450L
        private const val BOT_THINK_SPREAD_MS = 500L
        private const val RESIGN_CONFIRM_MS = 3_000L
        private const val DECLINED_SHOWN_MS = 2_500L
    }
}

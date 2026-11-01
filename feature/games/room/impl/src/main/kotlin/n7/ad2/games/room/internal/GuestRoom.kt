package n7.ad2.games.room.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import n7.ad2.games.room.CloseReason
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.PROTOCOL_VERSION
import n7.ad2.games.room.Player
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomCodec
import n7.ad2.games.room.RoomMessage
import n7.ad2.games.room.RoomState

/**
 * Комната у гостя: здоровается при подключении, в комнате - с первым [RoomMessage.Roster], где есть он сам,
 * дальше следует за хостом. Все сообщения идут через хост, он же пересылает их остальным.
 */
internal class GuestRoom(
    override val me: Player,
    private val transport: Transport,
    scope: CoroutineScope,
) : Room {

    private val _state = MutableStateFlow<RoomState>(RoomState.Connecting)
    override val state: StateFlow<RoomState> = _state.asStateFlow()
    private val _events = MutableSharedFlow<RoomMessage.GameEvent>(extraBufferCapacity = 64)
    override val events: Flow<RoomMessage.GameEvent> = _events.asSharedFlow()
    private val _syncEvents = MutableSharedFlow<RoomMessage.Sync>(extraBufferCapacity = 64)
    override val syncEvents: Flow<RoomMessage.Sync> = _syncEvents.asSharedFlow()

    init {
        scope.launch { transport.events.collect(::onEvent) }
    }

    override fun startGame(gameId: String, mode: GameMode) = error("Only the host starts a game")

    override fun endGame() = synchronized(this) {
        val playing = _state.value as? RoomState.Playing ?: return
        _state.value = RoomState.Ready(playing.players)
    }

    override fun sendEvent(payload: String) {
        check(_state.value is RoomState.Playing) { "Events can be sent only while Playing, state=${_state.value}" }
        send(RoomMessage.GameEvent(me.id, payload))
    }

    override fun sendSync(channel: String, payload: String) {
        val current = _state.value
        check(current is RoomState.Ready || current is RoomState.Playing) { "Sync can be sent only in Ready or Playing, state=$current" }
        send(RoomMessage.Sync(channel, payload, me.id))
    }

    override fun leave() = synchronized(this) {
        if (_state.value is RoomState.Closed) return
        send(RoomMessage.Leave(me.id))
        close(CloseReason.LEFT)
    }

    private fun onEvent(event: TransportEvent) {
        synchronized(this) {
            val current = _state.value
            if (current is RoomState.Closed) return
            when (event) {
                TransportEvent.Connected -> send(RoomMessage.Hello(me, PROTOCOL_VERSION))
                is TransportEvent.Text -> RoomCodec.decode(event.text)?.let(::onMessage)
                is TransportEvent.Closed -> _state.value = RoomState.Closed(if (current.isIn()) CloseReason.HOST_LEFT else CloseReason.CONNECTION_FAILED)
            }
        }
    }

    private fun onMessage(message: RoomMessage) {
        synchronized(this) {
            val current = _state.value
            if (current is RoomState.Closed) return
            when (message) {
                is RoomMessage.Roster -> if (message.players.any { it.id == me.id }) {
                    _state.value = if (current is RoomState.Playing) current.copy(players = message.players) else RoomState.Ready(message.players)
                }
                is RoomMessage.Reject -> close(
                    when (message.reason) {
                        RoomMessage.Reject.FULL -> CloseReason.FULL
                        RoomMessage.Reject.BUSY -> CloseReason.BUSY
                        else -> CloseReason.VERSION_MISMATCH
                    },
                )
                is RoomMessage.StartGame -> {
                    val players = (current as? RoomState.Ready)?.players ?: (current as? RoomState.Playing)?.players ?: return
                    _state.value = RoomState.Playing(players, message.gameId, message.mode, message.seed)
                }
                is RoomMessage.GameEvent -> if (message.playerId != me.id) _events.tryEmit(message)
                is RoomMessage.Sync -> if (message.playerId != me.id && current.isIn()) _syncEvents.tryEmit(message)
                is RoomMessage.Leave -> if (message.playerId == hostId(current)) close(CloseReason.HOST_LEFT)
                // так говорит только гость
                is RoomMessage.Hello -> Unit
            }
        }
    }

    private fun hostId(state: RoomState): String? = when (state) {
        is RoomState.Ready -> state.host.id
        is RoomState.Playing -> state.host.id
        else -> null
    }

    private fun RoomState.isIn() = this is RoomState.Ready || this is RoomState.Playing

    private fun send(message: RoomMessage) = transport.send(RoomCodec.encode(message))

    private fun close(reason: CloseReason) {
        _state.value = RoomState.Closed(reason)
        transport.close()
    }
}

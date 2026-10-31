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
import kotlin.random.Random

/**
 * Комната на двоих поверх [Transport]. Хост ждет [RoomMessage.Hello], отвечает [RoomMessage.Welcome] и запускает игру,
 * гость здоровается при подключении и следует за хостом.
 */
internal class RoomEngine(
    override val me: Player,
    private val isHost: Boolean,
    private val transport: Transport,
    scope: CoroutineScope,
    private val seedSource: () -> Long = { Random.nextLong() },
) : Room {

    private val _state = MutableStateFlow<RoomState>(RoomState.Connecting)
    override val state: StateFlow<RoomState> = _state.asStateFlow()
    private val _events = MutableSharedFlow<RoomMessage.GameEvent>(extraBufferCapacity = 64)
    override val events: Flow<RoomMessage.GameEvent> = _events.asSharedFlow()
    private val _syncEvents = MutableSharedFlow<RoomMessage.Sync>(extraBufferCapacity = 64)
    override val syncEvents: Flow<RoomMessage.Sync> = _syncEvents.asSharedFlow()

    init {
        scope.launch { transport.status.collect(::onStatus) }
        scope.launch { transport.incoming.collect { text -> RoomCodec.decode(text)?.let(::onMessage) } }
    }

    override fun startGame(gameId: String, mode: GameMode) = synchronized(this) {
        val ready = _state.value as? RoomState.Ready
        check(isHost && ready != null) { "Only the host can start a game in Ready, state=${_state.value}" }
        val seed = seedSource()
        send(RoomMessage.StartGame(gameId, mode, seed))
        _state.value = RoomState.Playing(ready.host, ready.guest, gameId, mode, seed)
    }

    // без сообщения: оба телефона заканчивают игру сами, а отставший гость примет следующий StartGame и из Playing
    override fun endGame() = synchronized(this) {
        val playing = _state.value as? RoomState.Playing ?: return
        _state.value = RoomState.Ready(playing.host, playing.guest)
    }

    override fun sendEvent(payload: String) {
        check(_state.value is RoomState.Playing) { "Events can be sent only while Playing, state=${_state.value}" }
        send(RoomMessage.GameEvent(me.id, payload))
    }

    override fun sendSync(channel: String, payload: String) {
        val current = _state.value
        check(current is RoomState.Ready || current is RoomState.Playing) { "Sync can be sent only in Ready or Playing, state=$current" }
        send(RoomMessage.Sync(channel, payload))
    }

    override fun leave() = synchronized(this) {
        if (_state.value is RoomState.Closed) return
        runCatching { send(RoomMessage.Leave(me.id)) }
        close(CloseReason.LEFT)
    }

    private fun onStatus(status: TransportStatus) = synchronized(this) {
        val current = _state.value
        if (current is RoomState.Closed) return
        when (status) {
            TransportStatus.Connecting -> Unit
            is TransportStatus.Listening -> if (isHost && current is RoomState.Connecting) _state.value = RoomState.Waiting(me, status.port)
            TransportStatus.Connected -> if (!isHost) send(RoomMessage.Hello(me, PROTOCOL_VERSION))
            is TransportStatus.Closed -> {
                val wasTogether = current is RoomState.Ready || current is RoomState.Playing
                _state.value = RoomState.Closed(if (wasTogether) CloseReason.PEER_LEFT else CloseReason.CONNECTION_FAILED)
            }
        }
    }

    private fun onMessage(message: RoomMessage) = synchronized(this) {
        val current = _state.value
        if (current is RoomState.Closed) return
        when (message) {
            is RoomMessage.Hello -> if (isHost && current is RoomState.Waiting) {
                if (message.protocolVersion != PROTOCOL_VERSION) {
                    send(RoomMessage.Reject("version"))
                } else {
                    send(RoomMessage.Welcome(me, message.player))
                    _state.value = RoomState.Ready(me, message.player)
                }
            }
            is RoomMessage.Welcome -> if (!isHost) _state.value = RoomState.Ready(message.host, message.guest)
            is RoomMessage.Reject -> if (!isHost) close(CloseReason.VERSION_MISMATCH)
            is RoomMessage.StartGame -> if (!isHost) {
                when (current) {
                    is RoomState.Ready -> _state.value = RoomState.Playing(current.host, current.guest, message.gameId, message.mode, message.seed)
                    is RoomState.Playing -> _state.value = RoomState.Playing(current.host, current.guest, message.gameId, message.mode, message.seed)
                    else -> Unit
                }
            }
            is RoomMessage.GameEvent -> if (message.playerId != me.id) _events.tryEmit(message)
            is RoomMessage.Leave -> close(CloseReason.PEER_LEFT)
            is RoomMessage.Sync -> if (current is RoomState.Ready || current is RoomState.Playing) _syncEvents.tryEmit(message)
        }
    }

    private fun send(message: RoomMessage) = transport.send(RoomCodec.encode(message))

    private fun close(reason: CloseReason) {
        _state.value = RoomState.Closed(reason)
        transport.close()
    }
}

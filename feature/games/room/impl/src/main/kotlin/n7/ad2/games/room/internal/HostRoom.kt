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
import n7.ad2.games.room.MAX_PLAYERS
import n7.ad2.games.room.PROTOCOL_VERSION
import n7.ad2.games.room.Player
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomCodec
import n7.ad2.games.room.RoomMessage
import n7.ad2.games.room.RoomState
import kotlin.random.Random

/**
 * Комната у хоста. Гость входит, сказав [RoomMessage.Hello]: хост добавляет его и рассылает всем [RoomMessage.Roster],
 * или отказывает - другая версия, комната полна, идет игра. События и sync гостя хост отдает себе и пересылает остальным
 * гостям, подставляя id отправителя по подключению. Ушли все гости - хост снова ждет в [RoomState.Waiting].
 */
internal class HostRoom(
    override val me: Player,
    private val transport: HostTransport,
    scope: CoroutineScope,
    private val seedSource: () -> Long = { Random.nextLong() },
) : Room {

    private val _state = MutableStateFlow<RoomState>(RoomState.Connecting)
    override val state: StateFlow<RoomState> = _state.asStateFlow()
    private val _events = MutableSharedFlow<RoomMessage.GameEvent>(extraBufferCapacity = 64)
    override val events: Flow<RoomMessage.GameEvent> = _events.asSharedFlow()
    private val _syncEvents = MutableSharedFlow<RoomMessage.Sync>(extraBufferCapacity = 64)
    override val syncEvents: Flow<RoomMessage.Sync> = _syncEvents.asSharedFlow()

    /** Гости по номерам подключений в порядке входа */
    private val guests = LinkedHashMap<Int, Player>()
    private var port = 0

    init {
        scope.launch { transport.status.collect(::onStatus) }
        scope.launch { transport.events.collect(::onEvent) }
    }

    override fun startGame(gameId: String, mode: GameMode) = synchronized(this) {
        val ready = _state.value as? RoomState.Ready
        checkNotNull(ready) { "A game starts in Ready, state=${_state.value}" }
        val seed = seedSource()
        broadcast(RoomMessage.StartGame(gameId, mode, seed))
        _state.value = RoomState.Playing(ready.players, gameId, mode, seed)
    }

    // без сообщения: каждый телефон заканчивает игру сам, отставший гость примет следующий StartGame и из Playing
    override fun endGame() = synchronized(this) {
        if (_state.value is RoomState.Playing) _state.value = together()
    }

    override fun sendEvent(payload: String) = synchronized(this) {
        check(_state.value is RoomState.Playing) { "Events can be sent only while Playing, state=${_state.value}" }
        broadcast(RoomMessage.GameEvent(me.id, payload))
    }

    override fun sendSync(channel: String, payload: String) = synchronized(this) {
        broadcast(RoomMessage.Sync(channel, payload, me.id))
    }

    override fun leave() = synchronized(this) {
        if (_state.value is RoomState.Closed) return
        broadcast(RoomMessage.Leave(me.id))
        close(CloseReason.LEFT)
    }

    private fun onStatus(status: HostStatus) {
        synchronized(this) {
            val current = _state.value
            if (current is RoomState.Closed) return
            when (status) {
                HostStatus.Starting -> Unit
                is HostStatus.Listening -> if (current is RoomState.Connecting) {
                    port = status.port
                    _state.value = RoomState.Waiting(me, port)
                }
                is HostStatus.Stopped -> _state.value = RoomState.Closed(CloseReason.CONNECTION_FAILED)
            }
        }
    }

    private fun onEvent(event: HostEvent) {
        synchronized(this) {
            if (_state.value is RoomState.Closed) return
            when (event) {
                // ждем Hello
                is HostEvent.Joined -> Unit
                is HostEvent.Text -> RoomCodec.decode(event.text)?.let { onMessage(event.client, it) }
                is HostEvent.Left -> if (guests.remove(event.client) != null) rosterChanged()
            }
        }
    }

    private fun onMessage(client: Int, message: RoomMessage) {
        val guest = guests[client]
        when (message) {
            is RoomMessage.Hello -> if (guest == null) hello(client, message)
            is RoomMessage.GameEvent -> if (guest != null) {
                val event = RoomMessage.GameEvent(guest.id, message.payload)
                broadcast(event, except = client)
                _events.tryEmit(event)
            }
            is RoomMessage.Sync -> if (guest != null) {
                val sync = message.copy(playerId = guest.id)
                broadcast(sync, except = client)
                _syncEvents.tryEmit(sync)
            }
            is RoomMessage.Leave -> if (guest != null) {
                guests.remove(client)
                transport.drop(client)
                rosterChanged()
            }
            // так говорит только хост
            is RoomMessage.Roster, is RoomMessage.Reject, is RoomMessage.StartGame -> Unit
        }
    }

    private fun hello(client: Int, hello: RoomMessage.Hello) {
        val reject = when {
            hello.protocolVersion != PROTOCOL_VERSION -> RoomMessage.Reject.VERSION
            guests.size + 1 >= MAX_PLAYERS -> RoomMessage.Reject.FULL
            _state.value is RoomState.Playing -> RoomMessage.Reject.BUSY
            else -> null
        }
        if (reject != null) {
            transport.send(client, RoomCodec.encode(RoomMessage.Reject(reject)))
            transport.drop(client)
            return
        }
        guests[client] = hello.player
        rosterChanged()
    }

    private fun rosterChanged() {
        val current = _state.value
        _state.value = if (current is RoomState.Playing) current.copy(players = players()) else together()
        broadcast(RoomMessage.Roster(players()))
    }

    private fun players(): List<Player> = listOf(me) + guests.values

    /** Вне игры: с гостями - Ready, один - снова ждет */
    private fun together(): RoomState = if (guests.isEmpty()) RoomState.Waiting(me, port) else RoomState.Ready(players())

    private fun broadcast(message: RoomMessage, except: Int? = null) {
        val text = RoomCodec.encode(message)
        guests.keys.filter { it != except }.forEach { client -> transport.send(client, text) }
    }

    private fun close(reason: CloseReason) {
        _state.value = RoomState.Closed(reason)
        transport.close()
    }
}

package n7.ad2.feature.games.xo.domain.internal.server.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import n7.ad2.feature.games.xo.domain.internal.server.FrameType
import n7.ad2.feature.games.xo.domain.internal.server.WebSocketFrames
import n7.ad2.feature.games.xo.domain.internal.server.WebSocketHandshake
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Websocket-сервер на несколько клиентов: у каждого подключения свой номер, его тексты приходят с этим номером.
 * Хост комнаты пересылает через него сообщения между гостями. Подключение без рукопожатия websocket закрывается,
 * не задевая остальных. [scope] - на IO: accept и чтение блокируют поток.
 */
class WebsocketHub(private val scope: CoroutineScope) {

    sealed interface Status {
        data object Starting : Status
        data class Listening(val port: Int) : Status

        /** [failed] - не удалось занять порт */
        data class Stopped(val failed: Boolean) : Status
    }

    sealed interface Event {
        data class Opened(val client: Int) : Event
        data class Text(val client: Int, val text: String) : Event
        data class Closed(val client: Int) : Event
    }

    private val _status = MutableStateFlow<Status>(Status.Starting)
    val status: StateFlow<Status> = _status.asStateFlow()

    private val _events = Channel<Event>(Channel.UNLIMITED)

    /** Один получатель - комната; события не теряются, пока он не подписался */
    val events: Flow<Event> = _events.receiveAsFlow()

    @Volatile private var server: ServerSocket? = null
    private val clients = ConcurrentHashMap<Int, Socket>()
    private val ids = AtomicInteger()

    /** Занимает свободный порт на [bindAddress] и принимает клиентов до [stop] */
    fun start(bindAddress: InetAddress) {
        scope.launch {
            val socket = try {
                ServerSocket(0, BACKLOG, bindAddress)
            } catch (e: IOException) {
                _status.value = Status.Stopped(failed = true)
                return@launch
            }
            server = socket
            _status.value = Status.Listening(socket.localPort)
            while (isActive) {
                val client = try {
                    socket.accept()
                } catch (e: IOException) {
                    break
                }
                val id = ids.incrementAndGet()
                clients[id] = client
                scope.launch { serve(id, client) }
            }
        }
    }

    /** Блокирует поток до записи; запись в одно подключение из разных потоков идет по очереди */
    fun send(client: Int, text: String) {
        val socket = clients[client] ?: return
        try {
            synchronized(socket) { WebSocketFrames.writeText(socket.getOutputStream(), text, mask = false) }
        } catch (e: IOException) {
            close(client)
        }
    }

    /** Закрывает подключение, клиент получит конец потока, а комната - [Event.Closed] */
    fun close(client: Int) {
        clients[client]?.let { runCatching { it.close() } }
    }

    fun stop() {
        runCatching { server?.close() }
        clients.values.forEach { runCatching { it.close() } }
        _status.value = Status.Stopped(failed = false)
    }

    private fun serve(id: Int, socket: Socket) {
        var opened = false
        try {
            val input = socket.getInputStream()
            WebSocketHandshake.accept(input, socket.getOutputStream())
            opened = true
            _events.trySend(Event.Opened(id))
            while (true) {
                when (val frame = WebSocketFrames.read(input)) {
                    FrameType.Close -> break
                    is FrameType.Text -> _events.trySend(Event.Text(id, frame.text))
                }
            }
        } catch (e: Exception) {
            // обрыв, чужой клиент без рукопожатия или закрытие нами - для комнаты одно и то же
        } finally {
            clients.remove(id)
            runCatching { socket.close() }
            if (opened) _events.trySend(Event.Closed(id))
        }
    }

    private companion object {
        const val BACKLOG = 16
    }
}

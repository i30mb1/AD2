package n7.ad2.feature.games.xo.domain.internal.server.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import n7.ad2.feature.games.xo.domain.internal.server.FrameType
import n7.ad2.feature.games.xo.domain.internal.server.WebSocketFrames
import n7.ad2.feature.games.xo.domain.internal.server.WebSocketHandshake
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Websocket-клиент гостя комнаты. Всё, что происходит с подключением, идет одной очередью [events] в том порядке,
 * в каком случилось: последнее сообщение хоста перед разрывом (например, отказ) не теряется и приходит раньше [Event.Closed].
 * [scope] - на IO: подключение и чтение блокируют поток.
 */
class WebsocketClient(private val scope: CoroutineScope) {

    sealed interface Event {
        data object Connected : Event
        data class Text(val text: String) : Event

        /** [failed] - подключиться не удалось вовсе */
        data class Closed(val failed: Boolean) : Event
    }

    private val _events = Channel<Event>(Channel.UNLIMITED)

    /** Один получатель - комната */
    val events: Flow<Event> = _events.receiveAsFlow()

    @Volatile private var socket: Socket? = null
    @Volatile private var closed = false

    fun connect(host: InetAddress, port: Int) {
        scope.launch {
            val socket = Socket()
            this@WebsocketClient.socket = socket
            try {
                socket.connect(InetSocketAddress(host, port), CONNECT_TIMEOUT_MS)
                WebSocketHandshake.request(socket.getInputStream(), socket.getOutputStream(), host.hostAddress.orEmpty(), port)
                // закрыли, пока подключались
                check(!closed)
            } catch (e: Exception) {
                runCatching { socket.close() }
                _events.trySend(Event.Closed(failed = true))
                return@launch
            }
            _events.trySend(Event.Connected)
            try {
                val input = socket.getInputStream()
                while (true) {
                    when (val frame = WebSocketFrames.read(input)) {
                        FrameType.Close -> break
                        is FrameType.Text -> _events.trySend(Event.Text(frame.text))
                    }
                }
            } catch (e: Exception) {
                // разрыв или закрытие нами - для комнаты одно и то же
            } finally {
                runCatching { socket.close() }
                _events.trySend(Event.Closed(failed = false))
            }
        }
    }

    /** Блокирует поток до записи; без подключения ничего не делает */
    fun send(text: String) {
        val socket = socket ?: return
        try {
            synchronized(socket) { WebSocketFrames.writeText(socket.getOutputStream(), text, mask = true) }
        } catch (e: IOException) {
            close()
        }
    }

    fun close() {
        closed = true
        runCatching { socket?.close() }
    }

    private companion object {
        const val CONNECT_TIMEOUT_MS = 5_000
    }
}

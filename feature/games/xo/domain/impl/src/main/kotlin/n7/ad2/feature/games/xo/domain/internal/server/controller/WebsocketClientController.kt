package n7.ad2.feature.games.xo.domain.internal.server.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import n7.ad2.feature.games.xo.domain.internal.server.FrameType
import n7.ad2.feature.games.xo.domain.internal.server.WebSocketFrames
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientState
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientStatus
import n7.ad2.feature.games.xo.domain.internal.server.data.Message
import n7.ad2.feature.games.xo.domain.internal.server.getBytes
import n7.ad2.feature.games.xo.domain.internal.server.socket.ClientCreatorImpl
import n7.ad2.feature.games.xo.domain.model.SimpleServer
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Base64
import java.util.UUID
import kotlin.experimental.xor

class WebsocketClientController(private val scope: CoroutineScope = CoroutineScope(Job() + newSingleThreadContext("WebsocketClientController"))) : ClientController {

    private val _state = MutableStateFlow(ClientState())
    override val state: StateFlow<ClientState> = _state
    private var socket: Socket? = null
    private val clientCreator = ClientCreatorImpl()

    override fun connect(name: String, ip: InetAddress, port: Int) {
        scope.launch {
            if (socket != null) error("Already connected")
            try {
                socket = clientCreator.create(ip, port)
            } catch (e: ClientCreatorImpl.Companion.ClientSocketException) {
                _state.update { it.copy(status = ClientStatus.Disconnected, messages = it.messages + Message.Info("Connect failed: ${ip.hostAddress}:$port")) }
                return@launch
            }
            val server = SimpleServer(name, ip.hostAddress!!, port)
            try {
                performWebSocketHandshake(requireNotNull(socket))
            } catch (e: Exception) {
                disconnect()
                _state.update { it.copy(messages = it.messages + Message.Info("Connect failed: ${ip.hostAddress}:$port")) }
                return@launch
            }
            // Connected только после handshake: иначе первое сообщение уйдет вперемешку с заголовками
            _state.update { it.copy(status = ClientStatus.Connected(server)) }
            collectMessages(requireNotNull(socket))
        }
    }

    private fun performWebSocketHandshake(socket: Socket) {
        val outputStream = socket.getOutputStream()
        val inputStream = socket.getInputStream()
        val writer = PrintWriter(outputStream)

        // Генерируем случайный ключ для WebSocket handshake
        val webSocketKey = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().toByteArray())

        // Отправляем HTTP запрос для upgrade до WebSocket
        writer.println("GET / HTTP/1.1")
        writer.println("Host: ${socket.inetAddress.hostAddress}:${socket.port}")
        writer.println("Upgrade: websocket")
        writer.println("Connection: Upgrade")
        writer.println("Sec-WebSocket-Key: $webSocketKey")
        writer.println("Sec-WebSocket-Version: 13")
        writer.println()
        writer.flush()

        // Читаем ответ сервера побайтно, чтобы не забрать фреймы, идущие следом
        WebSocketFrames.readHttpHeaderLines(inputStream)

        // В реальном приложении здесь нужно проверить Sec-WebSocket-Accept заголовок
        _state.update { it.copy(messages = it.messages + Message.Info("WebSocket handshake completed")) }
    }

    private suspend fun collectMessages(socket: Socket) = coroutineScope {
        val inputStream = socket.getInputStream()
        try {
            while (true) {
                ensureActive()
                val receivedFrame = WebSocketFrames.read(inputStream)
                when (receivedFrame) {
                    FrameType.Close -> {
                        disconnect()
                        break
                    }
                    is FrameType.Text -> {
                        val message = Message.Other(receivedFrame.text)
                        _state.update { it.copy(messages = it.messages + message) }
                    }
                }
            }
        } catch (e: Exception) {
            disconnect()
        }
    }

    override fun send(text: String) {
        val socket = socket ?: error("Not connected")
        val outputStream = socket.getOutputStream()
        WebSocketFrames.writeText(outputStream, text, mask = true)
        val message = Message.Me(text)
        _state.update { it.copy(messages = it.messages + message) }
    }

    override fun disconnect() {
        scope.coroutineContext.cancelChildren()
        runCatching { socket?.close() }
        socket = null
        _state.update { it.copy(status = ClientStatus.Disconnected, messages = emptyList()) }
    }
}

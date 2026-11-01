package n7.ad2.feature.games.xo.domain.internal.server.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import n7.ad2.feature.games.xo.domain.ServerCreator
import n7.ad2.feature.games.xo.domain.internal.server.FrameType
import n7.ad2.feature.games.xo.domain.internal.server.WebSocketFrames
import n7.ad2.feature.games.xo.domain.internal.server.WebSocketHandshake
import n7.ad2.feature.games.xo.domain.internal.server.data.Message
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerState
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerStatus
import n7.ad2.feature.games.xo.domain.internal.server.socket.ServerCreatorImpl
import n7.ad2.feature.games.xo.domain.model.SocketServerModel
import java.io.InputStream
import java.net.InetAddress
import java.net.Socket

class WebsocketServerController(private val scope: CoroutineScope = CoroutineScope(Job() + newSingleThreadContext("SocketServerController"))) : ServerController {

    private val _state: MutableStateFlow<ServerState> = MutableStateFlow(ServerState())
    override val state: StateFlow<ServerState> = _state
    private val serverCreator: ServerCreator = ServerCreatorImpl()
    private lateinit var server: SocketServerModel
    private var socket: Socket? = null

    override fun start(name: String, ip: InetAddress, port: Int) {
        scope.launch {
            try {
                server = serverCreator.create(name, ip, port)
                _state.update { it.copy(status = ServerStatus.Waiting(server)) }
                socket = server.serverSocket.accept()
                _state.update { it.copy(status = ServerStatus.Connected(server)) }
                collectMessages(requireNotNull(socket))
            } catch (e: Exception) {
                if (::server.isInitialized && !server.serverSocket.isClosed) {
                    _state.update { it.copy(status = ServerStatus.Closed, messages = emptyList()) }
                }
            }
        }
    }

    private fun collectMessages(socket: Socket) {
        scope.launch {
            try {
                val inputStream = socket.getInputStream()
                WebSocketHandshake.accept(inputStream, socket.getOutputStream())
                runWebSocketCommunication(inputStream)
                // клиент закрыл соединение (Close-фрейм или конец потока): сообщаем через статус
                stop()
            } catch (e: Exception) {
                stop()
            }
        }
    }

    override fun send(text: String) {
        val output = socket?.getOutputStream() ?: error("Server closed")
        WebSocketFrames.writeText(output, text, mask = false)
        _state.update { it.copy(messages = it.messages + Message.Me(text)) }
    }

    override fun stop() {
        scope.coroutineContext.cancelChildren()
        runCatching { socket?.close() }
        runCatching { if (::server.isInitialized) server.serverSocket.close() }
        socket = null
        _state.update { it.copy(status = ServerStatus.Closed, messages = emptyList()) }
    }

    private fun runWebSocketCommunication(input: InputStream) {
        while (true) {
            val receivedFrame = WebSocketFrames.read(input)
            when (receivedFrame) {
                FrameType.Close -> return
                is FrameType.Text -> {
                    _state.update { it.copy(messages = it.messages + Message.Other(receivedFrame.text)) }
                }
            }
        }
    }
}

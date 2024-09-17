@file:Suppress("RequireNotNullWithoutMessage")

package n7.ad2.feature.games.xo.domain.internal.server.controller

import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.util.Scanner
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
import n7.ad2.feature.games.xo.domain.ServerCreator
import n7.ad2.feature.games.xo.domain.internal.server.data.Message
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerState
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerStatus
import n7.ad2.feature.games.xo.domain.internal.server.socket.ServerCreatorImpl
import n7.ad2.feature.games.xo.domain.model.SocketServerModel

class SocketServerController(
    private val scope: CoroutineScope = CoroutineScope(Job() + newSingleThreadContext("SocketServerController")),
) : ServerController {

    private val _state: MutableStateFlow<ServerState> = MutableStateFlow(ServerState())
    override val state: StateFlow<ServerState> = _state
    private val serverCreator: ServerCreator = ServerCreatorImpl()
    private lateinit var server: SocketServerModel
    private var socket: Socket? = null

    override fun start(name: String, ip: InetAddress, port: Int) {
        scope.launch {
            server = serverCreator.create(name, ip, port)
            _state.update { it.copy(status = ServerStatus.Waiting(server)) }
            socket = server.serverSocket.accept()
            _state.update { it.copy(status = ServerStatus.Connected(server)) }
            collectClientMessages(requireNotNull(socket))
        }
    }

    private suspend fun collectClientMessages(socket: Socket) = coroutineScope {
        // тут надо узнать как клиент хочет с нами общаться
        val scanner = Scanner(socket.getInputStream())
        while (scanner.hasNext()) {
            ensureActive()
            val line = scanner.nextLine()
            val message = Message.Other(line)
            _state.update { it.copy(messages = it.messages + message) }
        }
        // клиент отключился, закрываем сервер
        stop()
    }

    override fun send(text: String) {
        // в зависимости какой у нас клиент надо отвечать по разному
        val socket = socket ?: error("Server closed")
        val writer = PrintWriter(socket.getOutputStream())

        writer.println(text)
        writer.flush()

        val message = Message.Me(text)
        _state.update { it.copy(messages = it.messages + message) }
    }

    override fun stop() {
        scope.coroutineContext.cancelChildren()
//        server.serverSocket.close()
        assert(server.serverSocket.isClosed)
        socket = null
        _state.update { it.copy(status = ServerStatus.Closed, messages = emptyList()) }
    }
}

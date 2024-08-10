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
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientState
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientStatus
import n7.ad2.feature.games.xo.domain.internal.server.data.Message
import n7.ad2.feature.games.xo.domain.internal.server.socket.ClientCreatorImpl
import n7.ad2.feature.games.xo.domain.model.SimpleServer

class SocketClientController(
    private val scope: CoroutineScope = CoroutineScope(Job() + newSingleThreadContext("SocketServerController")),
) : ClientController {
    private val _state = MutableStateFlow(ClientState())
    override val state: StateFlow<ClientState> = _state
    private var socket: Socket? = null
    private val clientCreator = ClientCreatorImpl()

    override fun connect(name: String, ip: InetAddress, port: Int) {
        scope.launch {
            if (socket != null) error("Already connected")
            socket = clientCreator.create(ip, port)
            val server = SimpleServer(name, ip.hostAddress!!, port)
            _state.update { it.copy(status = ClientStatus.Connected(server)) }
            collectClientMessages(requireNotNull(socket))
        }
    }

    private suspend fun collectClientMessages(socket: Socket) = coroutineScope {
        val scanner = Scanner(socket.getInputStream())
        while (scanner.hasNext()) {
            ensureActive()
            val line = scanner.nextLine()
            val message = Message.Other(line)
            _state.update { it.copy(messages = it.messages + message) }
        }
        // сервер отключился, закрываем соединение
        disconnect()
    }

    override fun send(text: String) {
        val socket = socket ?: error("Server closed")
        val writer = PrintWriter(socket.getOutputStream())

        writer.println(text)
        writer.flush()

        val message = Message.Me(text)
        _state.update { it.copy(messages = it.messages + message) }
    }

    override fun disconnect() {
        scope.coroutineContext.cancelChildren()
        socket?.close()
        socket = null
    }
}

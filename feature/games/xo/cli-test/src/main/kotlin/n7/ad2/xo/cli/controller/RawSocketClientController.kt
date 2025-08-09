package n7.ad2.xo.cli.controller

import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.util.Scanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.ad2.xo.cli.model.ClientState
import n7.ad2.xo.cli.model.ClientStatus
import n7.ad2.xo.cli.model.Message
import n7.ad2.xo.cli.model.SimpleServer

class RawSocketClientController : CliClientController {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val _state = MutableStateFlow(ClientState())
    override val state: StateFlow<ClientState> = _state

    private var socket: Socket? = null

    override suspend fun connect(name: String, ip: InetAddress, port: Int): Unit = withContext(Dispatchers.IO) {
        try {
            socket = Socket(ip, port)
            val server = SimpleServer(name, ip.hostAddress!!, port)
            _state.update {
                it.copy(
                    status = ClientStatus.Connected(server),
                    messages = it.messages + Message.Info("Connected to server")
                )
            }

            // Слушаем сообщения от сервера
            scope.launch {
                listenForMessages()
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    status = ClientStatus.Disconnected,
                    messages = it.messages + Message.Info("Failed to connect: ${e.message}")
                )
            }
        }
    }

    private suspend fun listenForMessages() = withContext(Dispatchers.IO) {
        try {
            val scanner = Scanner(socket!!.getInputStream())
            while (scanner.hasNextLine() && socket?.isConnected == true) {
                val message = scanner.nextLine()
                _state.update {
                    it.copy(messages = it.messages + Message.Other("Server: $message"))
                }
                println("📨 Received: $message")
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    status = ClientStatus.Disconnected,
                    messages = it.messages + Message.Info("Connection lost: ${e.message}")
                )
            }
        }
    }

    override suspend fun send(text: String) = withContext(Dispatchers.IO) {
        try {
            val writer = PrintWriter(socket!!.getOutputStream(), true)
            writer.println(text)
            _state.update {
                it.copy(messages = it.messages + Message.Me("Client: $text"))
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(messages = it.messages + Message.Info("Failed to send message: ${e.message}"))
            }
        }
    }

    override fun disconnect() {
        scope.cancel()
        runCatching { socket?.close() }
        _state.update { it.copy(status = ClientStatus.Disconnected, messages = emptyList()) }
    }
}

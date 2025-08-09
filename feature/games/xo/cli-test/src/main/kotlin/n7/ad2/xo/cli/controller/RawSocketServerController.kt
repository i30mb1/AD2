package n7.ad2.xo.cli.controller

import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
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
import n7.ad2.xo.cli.model.Message
import n7.ad2.xo.cli.model.ServerState
import n7.ad2.xo.cli.model.ServerStatus
import n7.ad2.xo.cli.model.SimpleServer

class RawSocketServerController : CliServerController {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val _state = MutableStateFlow(ServerState())
    override val state: StateFlow<ServerState> = _state

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null

    override suspend fun start(name: String, ip: InetAddress, port: Int): Unit = withContext(Dispatchers.IO) {
        try {
            serverSocket = ServerSocket(port, 0, ip)
            val actualPort = serverSocket!!.localPort
            val actualIp = serverSocket!!.inetAddress.hostAddress!!

            val server = SimpleServer(name, actualIp, actualPort)
            _state.update { it.copy(status = ServerStatus.Waiting(server)) }

            // Ждем подключения клиента
            scope.launch {
                try {
                    clientSocket = serverSocket!!.accept()
                    _state.update {
                        it.copy(
                            status = ServerStatus.Connected(server),
                            messages = it.messages + Message.Info("Client connected")
                        )
                    }

                    // Слушаем сообщения от клиента
                    listenForMessages()
                } catch (e: Exception) {
                    if (!serverSocket!!.isClosed) {
                        _state.update {
                            it.copy(
                                status = ServerStatus.Closed,
                                messages = it.messages + Message.Info("Server error: ${e.message}")
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    status = ServerStatus.Closed,
                    messages = it.messages + Message.Info("Failed to start server: ${e.message}")
                )
            }
        }
    }

    private suspend fun listenForMessages() = withContext(Dispatchers.IO) {
        try {
            val scanner = Scanner(clientSocket!!.getInputStream())
            while (scanner.hasNextLine() && clientSocket?.isConnected == true) {
                val message = scanner.nextLine()
                _state.update {
                    it.copy(messages = it.messages + Message.Other("Client: $message"))
                }
                println("📨 Received: $message")
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(messages = it.messages + Message.Info("Connection lost: ${e.message}"))
            }
        }
    }

    override suspend fun send(text: String) = withContext(Dispatchers.IO) {
        try {
            val writer = PrintWriter(clientSocket!!.getOutputStream(), true)
            writer.println(text)
            _state.update {
                it.copy(messages = it.messages + Message.Me("Server: $text"))
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(messages = it.messages + Message.Info("Failed to send message: ${e.message}"))
            }
        }
    }

    override fun stop() {
        scope.cancel()
        runCatching { clientSocket?.close() }
        runCatching { serverSocket?.close() }
        _state.update { it.copy(status = ServerStatus.Closed, messages = emptyList()) }
    }
}

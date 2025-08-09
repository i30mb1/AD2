package n7.ad2.xo.cli.controller

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.ad2.xo.cli.model.ClientState
import n7.ad2.xo.cli.model.ClientStatus
import n7.ad2.xo.cli.model.Message
import n7.ad2.xo.cli.model.SimpleServer

class HttpClientController : CliClientController {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val _state = MutableStateFlow(ClientState())
    override val state: StateFlow<ClientState> = _state

    private var serverInfo: SimpleServer? = null

    override suspend fun connect(name: String, ip: InetAddress, port: Int): Unit {
        serverInfo = SimpleServer(name, ip.hostAddress!!, port)
        _state.update {
            it.copy(
                status = ClientStatus.Connected(serverInfo!!),
                messages = it.messages + Message.Info("Connected to HTTP server")
            )
        }

        // Запускаем polling для получения сообщений
        scope.launch {
            startPolling()
        }
    }

    private suspend fun startPolling() = withContext(Dispatchers.IO) {
        while (serverInfo != null) {
            try {
                val message = performGetRequest("/")
                if (message.isNotEmpty() && message != "No messages") {
                    _state.update {
                        it.copy(messages = it.messages + Message.Other("HTTP GET: $message"))
                    }
                    println("📨 Received: $message")
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(messages = it.messages + Message.Info("Polling error: ${e.message}"))
                }
            }
            delay(2000) // Poll every 2 seconds
        }
    }

    private suspend fun performGetRequest(path: String): String = withContext(Dispatchers.IO) {
        val server = serverInfo ?: return@withContext ""

        try {
            val socket = Socket(server.ip, server.port)
            val writer = PrintWriter(socket.getOutputStream())
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Send GET request
            writer.println("GET $path HTTP/1.1")
            writer.println("Host: ${server.ip}:${server.port}")
            writer.println("Connection: close")
            writer.println()
            writer.flush()

            // Read response
            skipHttpHeaders(reader)
            val response = reader.readLine() ?: ""

            writer.close()
            reader.close()
            socket.close()

            response
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    override suspend fun send(text: String) = withContext(Dispatchers.IO) {
        val server = serverInfo ?: return@withContext

        try {
            val socket = Socket(server.ip, server.port)
            val writer = PrintWriter(socket.getOutputStream())
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Send POST request
            writer.println("POST / HTTP/1.1")
            writer.println("Host: ${server.ip}:${server.port}")
            writer.println("Content-Type: text/plain")
            writer.println("Content-Length: ${text.length}")
            writer.println("Connection: close")
            writer.println()
            writer.println(text)
            writer.flush()

            // Read response
            skipHttpHeaders(reader)
            val response = reader.readLine() ?: ""

            _state.update {
                it.copy(messages = it.messages + Message.Me("HTTP POST: $text"))
            }

            if (response.isNotEmpty()) {
                _state.update {
                    it.copy(messages = it.messages + Message.Info("Server response: $response"))
                }
            }

            writer.close()
            reader.close()
            socket.close()
        } catch (e: Exception) {
            _state.update {
                it.copy(messages = it.messages + Message.Info("Send error: ${e.message}"))
            }
        }
    }

    private fun skipHttpHeaders(reader: BufferedReader) {
        var line = reader.readLine()
        while (line != null && line.isNotEmpty()) {
            line = reader.readLine()
        }
    }

    override fun disconnect() {
        scope.cancel()
        serverInfo = null
        _state.update { it.copy(status = ClientStatus.Disconnected, messages = emptyList()) }
    }
}

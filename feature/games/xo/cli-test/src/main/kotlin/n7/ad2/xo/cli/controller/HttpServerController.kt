package n7.ad2.xo.cli.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.ad2.xo.cli.model.Message
import n7.ad2.xo.cli.model.ServerState
import n7.ad2.xo.cli.model.ServerStatus
import n7.ad2.xo.cli.model.SimpleServer
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.util.Scanner

class HttpServerController : CliServerController {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val _state = MutableStateFlow(ServerState())
    override val state: StateFlow<ServerState> = _state

    private var serverSocket: ServerSocket? = null
    private val messagesToSend = Channel<String>(Channel.BUFFERED)

    override suspend fun start(name: String, ip: InetAddress, port: Int): Unit = withContext(Dispatchers.IO) {
        try {
            messagesToSend.trySend("Hello from HTTP server!")

            serverSocket = ServerSocket(port, 0, ip)
            val actualPort = serverSocket!!.localPort
            val actualIp = serverSocket!!.inetAddress.hostAddress!!

            val server = SimpleServer(name, actualIp, actualPort)
            _state.update {
                it.copy(
                    status = ServerStatus.Connected(server),
                    messages = it.messages + Message.Info("HTTP server started"),
                )
            }

            // Обрабатываем HTTP запросы
            scope.launch {
                handleHttpRequests()
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    status = ServerStatus.Closed,
                    messages = it.messages + Message.Info("Failed to start HTTP server: ${e.message}"),
                )
            }
        }
    }

    private suspend fun handleHttpRequests() = withContext(Dispatchers.IO) {
        while (serverSocket?.isClosed == false) {
            try {
                val clientSocket = serverSocket!!.accept()
                launch {
                    val writer = PrintWriter(clientSocket.getOutputStream())
                    val reader = Scanner(clientSocket.getInputStream())

                    if (reader.hasNextLine()) {
                        val requestLine = reader.nextLine()
                        val requestParts = requestLine.split(" ")

                        if (requestParts.size >= 2) {
                            val method = requestParts[0]
                            val path = requestParts[1]

                            // Читаем заголовки
                            val headers = mutableMapOf<String, String>()
                            var line = reader.nextLine()
                            while (line.isNotEmpty() && reader.hasNextLine()) {
                                val headerParts = line.split(":", limit = 2)
                                if (headerParts.size == 2) {
                                    headers[headerParts[0].trim()] = headerParts[1].trim()
                                }
                                line = reader.nextLine()
                            }

                            _state.update {
                                it.copy(messages = it.messages + Message.Info("HTTP $method $path"))
                            }
                            println("📨 HTTP Request: $method $path")

                            when (method) {
                                "GET" -> {
                                    val response = when (path) {
                                        "/" -> messagesToSend.tryReceive().getOrNull() ?: "No messages"
                                        else -> "Not found"
                                    }
                                    sendHttpResponse(writer, response)
                                }

                                "POST" -> {
                                    val contentLength = headers["Content-Length"]?.toIntOrNull() ?: 0
                                    if (contentLength > 0 && reader.hasNext()) {
                                        val body = reader.next()
                                        _state.update {
                                            it.copy(messages = it.messages + Message.Other("HTTP POST: $body"))
                                        }
                                        println("📨 Received POST data: $body")
                                    }
                                    sendHttpResponse(writer, "Message received")
                                }
                            }
                        }
                    }

                    writer.close()
                    reader.close()
                    clientSocket.close()
                }
            } catch (e: Exception) {
                if (serverSocket?.isClosed == false) {
                    _state.update {
                        it.copy(messages = it.messages + Message.Info("HTTP error: ${e.message}"))
                    }
                }
            }
        }
    }

    private fun sendHttpResponse(writer: PrintWriter, content: String) {
        writer.println("HTTP/1.1 200 OK")
        writer.println("Content-Type: text/plain; charset=UTF-8")
        writer.println("Access-Control-Allow-Origin: *")
        writer.println("Content-Length: ${content.length}")
        writer.println()
        writer.println(content)
        writer.flush()
    }

    override suspend fun send(text: String) {
        messagesToSend.trySend(text)
        _state.update {
            it.copy(messages = it.messages + Message.Me("HTTP: $text"))
        }
    }

    override fun stop() {
        scope.cancel()
        runCatching { serverSocket?.close() }
        _state.update { it.copy(status = ServerStatus.Closed, messages = emptyList()) }
    }
}

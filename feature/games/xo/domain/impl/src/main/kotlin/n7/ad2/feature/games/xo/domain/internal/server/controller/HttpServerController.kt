@file:Suppress("DoubleExclamationUsage")

package n7.ad2.feature.games.xo.domain.internal.server.controller

import java.io.PrintWriter
import java.net.InetAddress
import java.util.Scanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
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

class HttpServerController : ServerController {
    private val scope = CoroutineScope(Job() + newSingleThreadContext("SocketServerController"))
    private val _state: MutableStateFlow<ServerState> = MutableStateFlow(ServerState())
    override val state: StateFlow<ServerState> = _state
    private val serverCreator: ServerCreator = ServerCreatorImpl()
    private val messagesToSend = Channel<String>(capacity = Channel.BUFFERED)
    private lateinit var server: SocketServerModel

    override fun start(name: String, ip: InetAddress, port: Int) {
        messagesToSend.trySend("Hello Client!")
        scope.launch {
            try {
                server = serverCreator.create(name, ip, port)
                _state.update { it.copy(status = ServerStatus.Connected(server)) }
                collectMessages()
            } catch (e: Exception) {
                if (::server.isInitialized && !server.serverSocket.isClosed) {
                    _state.update { it.copy(status = ServerStatus.Closed, messages = emptyList()) }
                }
            }
        }
    }

    private suspend fun collectMessages() = coroutineScope {
        try {
            while (true) {
                val socket = server.serverSocket.accept()
            val writer = PrintWriter(socket.getOutputStream())
            val inputStream = socket.getInputStream()
            val reader = Scanner(inputStream)
            if (!reader.hasNext()) {
                socket.close()
                writer.close()
                reader.close()
                continue
            }
            val request = requestParts(reader)
            // Читаем заголовки
            val headers = readHeaders(reader)

            _state.update { it.copy(messages = it.messages + Message.Info("request: ${request.toList().joinToString("\n")}")) }
            when (request["method"]) {
                "GET" -> {
                    var response: String
                    when (request["resource"]) {
                        "/" -> response = messagesToSend.receive()
                        "/favicon.ico" -> response = "empty"
                        else -> error("!")
                    }
                    sendResponse(writer, response)
                }

                "POST" -> {
                    val messageLength = headers["Content-Length"]!!.toInt()
                    val requestBody = reader.next()
                    _state.update { it.copy(messages = it.messages + Message.Other(requestBody.toString())) }
                    // Ответ серверу
                    val response = "POST request received"
                    sendResponse(writer, response)
                }

                else -> error("???")
            }
                writer.close()
                reader.close()
                socket.close()
            }
        } catch (e: Exception) {
            // Нормальное завершение при закрытии сервера
        }
    }

    private fun sendResponse(writer: PrintWriter, response: String) {
        writer.println("HTTP/1.1 200 OK")
        writer.println("Content-Type: text/plain; charset=UTF-8")
        writer.println("Access-Control-Allow-Origin: *")
        writer.println("Content-Length: ${response.length}")
        writer.println()
        writer.println(response)
        writer.flush()
    }

    override fun send(text: String) {
        _state.update { it.copy(messages = it.messages + Message.Me(text)) }
        messagesToSend.trySend(text)
    }

    override fun stop() {
        scope.coroutineContext.cancelChildren()
        runCatching { if (::server.isInitialized) server.serverSocket.close() }
        _state.update { it.copy(status = ServerStatus.Closed, messages = emptyList()) }
    }

    private fun requestParts(reader: Scanner): Map<String, String> = buildMap {
        val request = reader.nextLine()
        val requestParts = request.split(" ")
        put("method", requestParts[0]) // Метод запроса (GET, POST и т.д.)
        put("resource", requestParts[1]) // Запрашиваемый ресурс (URL)
        put("httpVersion", requestParts[2]) // Версия протокола
    }

    private fun readHeaders(reader: Scanner): MutableMap<String, String> {
        val headers = mutableMapOf<String, String>()
        var line = reader.nextLine()
        while (line.isNotEmpty()) {
            val headerParts = line.split(":")
            if (headerParts.size >= 2) {
                val headerName = headerParts[0].trim()
                val headerValue = headerParts[1].trim()
                headers[headerName] = headerValue
            }
            line = reader.nextLine()
        }
        return headers
    }
}
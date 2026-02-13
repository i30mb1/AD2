package n7.ad2.feature.games.xo.domain.internal.server.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress

class HttpClientController(private val scope: CoroutineScope = CoroutineScope(Job() + newSingleThreadContext("HttpClientController"))) : ClientController {

    private val _state = MutableStateFlow(ClientState())
    override val state: StateFlow<ClientState> = _state
    private var serverInfo: SimpleServer? = null
    private val clientCreator = ClientCreatorImpl()

    override fun connect(name: String, ip: InetAddress, port: Int) {
        scope.launch {
            if (serverInfo != null) error("Already connected")
            serverInfo = SimpleServer(name, ip.hostAddress!!, port)
            _state.update { it.copy(status = ClientStatus.Connected(serverInfo!!)) }

            // Для HTTP клиента сразу начинаем polling для получения сообщений
            startPollingMessages()
        }
    }

    private suspend fun startPollingMessages() {
        while (serverInfo != null) {
            try {
                val message = performGetRequest("/")
                if (message.isNotEmpty() && message != "empty") {
                    _state.update { it.copy(messages = it.messages + Message.Other(message)) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(messages = it.messages + Message.Info("Error polling: ${e.message}")) }
            }
            delay(1000) // Polling каждую секунду
        }
    }

    override fun send(text: String) {
        val server = serverInfo ?: error("Not connected")
        scope.launch {
            try {
                performPostRequest(text)
                val message = Message.Me(text)
                _state.update { it.copy(messages = it.messages + message) }
            } catch (e: Exception) {
                _state.update { it.copy(messages = it.messages + Message.Info("Error sending: ${e.message}")) }
            }
        }
    }

    override fun disconnect() {
        scope.coroutineContext.cancelChildren()
        serverInfo = null
        _state.update { it.copy(status = ClientStatus.Disconnected, messages = emptyList()) }
    }

    private suspend fun performGetRequest(path: String): String {
        val server = serverInfo ?: error("Not connected")
        val socket = clientCreator.create(InetAddress.getByName(server.ip), server.port)

        return try {
            val writer = PrintWriter(socket.getOutputStream())
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Отправляем GET запрос
            writer.println("GET $path HTTP/1.1")
            writer.println("Host: ${server.ip}:${server.port}")
            writer.println("Connection: close")
            writer.println()
            writer.flush()

            // Читаем ответ
            skipHttpHeaders(reader)
            reader.readLine() ?: ""
        } finally {
            socket.close()
        }
    }

    private suspend fun performPostRequest(body: String) {
        val server = serverInfo ?: error("Not connected")
        val socket = clientCreator.create(InetAddress.getByName(server.ip), server.port)

        try {
            val writer = PrintWriter(socket.getOutputStream())
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Отправляем POST запрос
            writer.println("POST / HTTP/1.1")
            writer.println("Host: ${server.ip}:${server.port}")
            writer.println("Content-Type: text/plain")
            writer.println("Content-Length: ${body.length}")
            writer.println("Connection: close")
            writer.println()
            writer.println(body)
            writer.flush()

            // Читаем ответ для подтверждения
            skipHttpHeaders(reader)
            val response = reader.readLine()
            _state.update { it.copy(messages = it.messages + Message.Info("Server response: $response")) }
        } finally {
            socket.close()
        }
    }

    private fun skipHttpHeaders(reader: BufferedReader) {
        // Пропускаем HTTP заголовки до пустой строки
        var line = reader.readLine()
        while (line.isNotEmpty()) {
            line = reader.readLine()
        }
    }
}

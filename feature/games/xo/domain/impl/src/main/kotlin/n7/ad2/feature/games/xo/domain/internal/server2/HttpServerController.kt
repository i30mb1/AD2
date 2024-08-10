package n7.ad2.feature.games.xo.domain.internal.server2

import java.io.PrintWriter
import java.net.InetAddress
import java.util.Scanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import n7.ad2.feature.games.xo.domain.ServerCreator
import n7.ad2.feature.games.xo.domain.internal.server.socket.ServerCreatorImpl
import n7.ad2.feature.games.xo.domain.internal.server2.data.ServerState
import n7.ad2.feature.games.xo.domain.internal.server2.data.ServerStatus
import n7.ad2.feature.games.xo.domain.model.SocketServerModel

class HttpServerController : ServerController {
    private val scope = CoroutineScope(Job() + newSingleThreadContext("SocketServerController"))
    private val _state: MutableStateFlow<ServerState> = MutableStateFlow(ServerState())
    override val state: StateFlow<ServerState> = _state
    private val serverCreator: ServerCreator = ServerCreatorImpl()
    private val messagesToSend = Channel<String>()
    private lateinit var server: SocketServerModel

    override fun start(name: String, ip: InetAddress, port: Int) {
        scope.launch {
            server = serverCreator.create(name, ip, port)
            _state.update { it.copy(status = ServerStatus.Connected(server)) }
        }
    }

    fun collectMessages() = scope.launch {
        val socket = server.serverSocket.accept()
        val writer = PrintWriter(socket.getOutputStream())
        val reader = Scanner(socket.getInputStream())
        val request = requestParts(reader)
        // Читаем заголовки
        val headers = readHeaders(reader)

        when (request["method"]) {
            "GET" -> {
                // Отправляем сообщение когда будет что отправить
                val response = messagesToSend.receive()
                writer.println("HTTP/1.1 200 OK")
                writer.println("Content-Type: text/plain")
                writer.println("Access-Control-Allow-Origin: *")
                writer.println("Content-Length: ${response.length}")
                writer.println()
                writer.println(response)

                writer.flush()
            }

            "POST" -> {
                val messageLenght = headers[""]
                reader
            }
        }
    }

    override fun send(text: String) {

    }

    override fun stop() {

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
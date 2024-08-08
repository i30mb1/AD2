package n7.ad2.feature.games.xo.domain.internal.server.socket

import java.io.PrintWriter
import java.net.Socket
import java.util.Scanner
import kotlinx.coroutines.channels.Channel
import n7.ad2.feature.games.xo.domain.SocketMessanger

class GameHttpSocketMessenger : SocketMessanger {

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: Scanner? = null
    private val messagesToSend = Channel<String>()
    private val incomingMessages = Channel<String>()

    override fun init(socket: Socket) {
        this.socket = socket
        writer = PrintWriter(socket.getOutputStream())
        reader = Scanner(socket.getInputStream())
    }

    override fun isConnected(): Boolean {
        return socket != null
    }

    override suspend fun awaitMessage(): String {
        val writer = writer ?: error("call init first")
        val reader = reader ?: error("call init first")
        val request = requestParts(reader)
        // (2.2) Читаем заголовки
        val headers = readHeaders(reader)

        // (2.3) Отправляем сообщение
        val response = messagesToSend.receive()
        writer.println("HTTP/1.1 200 OK")
        writer.println("Content-Type: text/plain")
        writer.println("Access-Control-Allow-Origin: *")
        writer.println("Content-Length: ${response.length}")
        writer.println()
        writer.println(response)

        writer.flush()

        return incomingMessages.receive()
    }

    override fun sendMessage(message: String) {
        messagesToSend.trySend(message)
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

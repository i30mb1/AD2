package n7.ad2.games.demo.server

import com.google.android.gms.common.util.Base64Utils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.security.MessageDigest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class GameServer(
    private val logger: (message: String) -> Unit,
) {

    private class GameServerError(message: String) : Exception(message)

    private val scope = CoroutineScope(Job())

    companion object {
        const val host = "192.168.100.8"
        val ports = intArrayOf(50088, 50001)
    }

    fun run() = scope.launch(Dispatchers.IO) {
        try {
            val server = openServer()
            val clientSocket = server.accept()
            handleClient(clientSocket)
        } catch (e: Exception) {
            logger(e.toString())
        }
    }

    private fun handleClient(clientSocket: Socket) {
        val inputStream = clientSocket.getInputStream()
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        val headers = readHeaders(bufferedReader)

        val outputStream = clientSocket.getOutputStream()
        val writer = PrintWriter(outputStream, true)

        when (prepareHandshake(headers)) {
            HandshakeType.HTTP -> TODO()
            HandshakeType.WEB_SOCKET -> handshakeWebSocket(headers, writer)
        }
    }

    /**
     * Согласно RFC-6455#section-1.3 чтобы доказать рукопожатие нужно:
     * 1) взять значение заголовка [Sec-WebSocket-Key] обьединить его с 258EAFA5-E914-47DA-95CA-C5AB0DC85B11
     * 2) захэшировать SHA-1 алгоритмом полученную строку
     * 3) полученные двоичные данные закодировать Base64 кодировкой
     * и полученные данные отправить клиенту под заголовком [Sec-WebSocket-Accept]
     *
     * Первой строкой HTTP/1.1 101 Switching Protocols
     * Строки
     * [Upgrade: websocket]
     * [Connection: Upgrade]
     * чтобы завершить HTTP Upgrade
     */
    private fun handshakeWebSocket(headers: Map<String, String>, writer: PrintWriter) {
        val guid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
        val key = headers["Sec-WebSocket-Key"] + guid
        val hash: ByteArray = MessageDigest.getInstance("SHA-1").digest(key.toByteArray())
        val base64 = Base64Utils.encode(hash)

        writer.println("HTTP/1.1 101 Switching Protocols")
        writer.println("Upgrade: websocket")
        writer.println("Connection: Upgrade")
        writer.println()
    }

    private fun prepareHandshake(headers: Map<String, String>): HandshakeType {
        return when (val value = headers["Upgrade"]) {
            "websocket" -> HandshakeType.WEB_SOCKET
            "http" -> HandshakeType.HTTP
            else -> throw GameServerError("Unsupported Upgrade type: [$value]")
        }
    }

    /**
     * @param reader [BufferedReader] Читаем HTTP-заголовки из возвращаем в виде
     * @return [Map] с заголовками, где ключ - имя заголовка, значение - значение заголовка
     *
     * Примечание: Функция испоьлзует метод split(":", limit = 2), где параметр [limit] ограничивает разделение строки только двумя частями
     * Согласно RFC-822#section-3.2 это предпологает, что строки имеют формат "name: value"
     */
    private fun readHeaders(reader: BufferedReader): Map<String, String> = buildMap {
        var line: String? = reader.readLine()
        while (!line.isNullOrEmpty()) {
            val headerParts = line.split(":", limit = 2)
            if (headerParts.size == 2) {
                val headerName = headerParts[0].trim()
                val headerValue = headerParts[1].trim()
                if (headerName.isNotBlank() && headerValue.isNotBlank()) {
                    put(headerName, headerValue)
                }
            }
            line = reader.readLine()
        }
    }

    private fun openServer(): ServerSocket {
        for (port in ports) {
            try {
                val id = getIPAddress()
                return ServerSocket(port, 0, InetAddress.getByName(host))
            } catch (e: Exception) {
                logger("could not open port: [$port]")

            }
        }
        throw GameServerError("Open Server failed")
    }

    private fun getIPAddress(): String {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val adress = inetAddresses.nextElement()
                if (!adress.isLoopbackAddress && !adress.isLinkLocalAddress && adress.isSiteLocalAddress) {
                    return adress.hostAddress
                }
            }
        }
        error("")
    }
}

suspend fun main() {
//    runBlocking {
//        GameServer().run()
//    }.join()
}

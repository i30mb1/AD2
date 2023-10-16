package n7.ad2.games.demo.server

import com.google.android.gms.common.util.Base64Utils
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.security.MessageDigest
import kotlin.experimental.xor
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
        private const val B0_MASK_FIN = 0b10000000
        private const val B1_MASK_MASK = 0b10000000
        private const val B1_MASK_LENGTH = 0b01111111
        private const val PAYLOAD_SHORT = 126L
        private const val PAYLOAD_LONG = 127L
    }

    fun run() = scope.launch(Dispatchers.IO) {
        try {
            val server = openServer()
            val clientSocket = server.accept()
            logger("Connection accepted")
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
            HandshakeType.HTTP -> {
                TODO()
            }

            HandshakeType.WEB_SOCKET -> {
                handshakeWebSocket(headers, writer)
                runWebSocketCommunication(inputStream, outputStream)
            }
        }
    }

    private fun runWebSocketCommunication(inputStream: InputStream, outputStream: OutputStream) {
        while (true) {
            val frame = readSocketFrame(inputStream)
        }
    }

    /**
     *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-------+-+-------------+-------------------------------+
     * |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
     * |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
     * |N|V|V|V|       |S|             |   (if payload len==126/127)   |
     * | |1|2|3|       |K|             |                               |
     * +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
     * |     Extended payload length continued, if payload len == 127  |
     * + - - - - - - - - - - - - - - - +-------------------------------+
     * |                               |Masking-key, if MASK set to 1  |
     * +-------------------------------+-------------------------------+
     * | Masking-key (continued)       |          Payload Data         |
     * +-------------------------------- - - - - - - - - - - - - - - - +
     * :                     Payload Data continued ...                :
     * + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
     * |                     Payload Data continued ...                |
     * +---------------------------------------------------------------+
     *
     * [isMasked] According to RFC-6455#sedtion-5.2 All frames sent from client to server have this bit set to 1
     */
    private fun readSocketFrame(input: InputStream): String {
        val b0: Int = input.read()
        val isFinalFrame = b0 and B0_MASK_FIN

        when (getOpCode(b0)) {
            OpCode.OPCODE_TEXT -> TODO()
        }

        val b1 = input.read()
        val isMasked = b1 and B1_MASK_MASK != 0
        var frameLength: Long = (b1 and B1_MASK_LENGTH).toLong()
        when (frameLength) {
            PAYLOAD_SHORT -> {
                val byte = input.getBytes(2)
                frameLength = (byte[0].toLong() shl 8) or byte[1].toLong()
                TODO("Поддержать длину payload в 16бит")
            }

            PAYLOAD_LONG -> TODO("Поддержать длину payload в 64бит")
        }

        val maskKey = if (isMasked) {
            input.getBytes(4)
        } else {
            ByteArray(0)
        }
        val payload = input.getBytes(frameLength)
        if (isMasked) {
            unmaskedPayload(payload, maskKey)
        }

        return String(payload)
    }

    private fun unmaskedPayload(payload: ByteArray, maskKey: ByteArray) {
        for (index in payload.indices) {
            payload[index] = payload[index] xor maskKey[index % 4]
        }
    }

    /**
     * Согласно RFC-6455#section-1.3 чтобы доказать рукопожатие нужно:
     * 1) взять значение заголовка [Sec-WebSocket-Key] обьединить его с 258EAFA5-E914-47DA-95CA-C5AB0DC85B11
     * 2) захэшировать SHA-1 алгоритмом полученную строку
     * 3) полученные двоичные данные закодировать Base64 кодировкой
     * и полученные данные отправить клиенту под заголовком [Sec-WebSocket-Accept]
     *
     * Ответ сервера для установления websocket соединения
     * [HTTP/1.1 101 Switching Protocols] - говорит клиенту что сервер переключился на нужный протокол указанный в Upgrade заголовке
     * [Upgrade: websocket] - подтверждаем что соеднинение будет переключенно на новый протокол
     * [Connection: Upgrade] - подтверждаем что соеднинение будет переключенно на новый протокол
     * [Sec-WebSocket-Accept] - ^
     *
     * Клиент в свою очередь генерирует Sec-WebSocket-Key таким образом:
     * ByteArray(16).apply { Random.nextBytes(this) }.toByteString().base64()
     */
    private fun handshakeWebSocket(headers: Map<String, String>, writer: PrintWriter) {
        val guid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
        val key = headers["Sec-WebSocket-Key"] + guid
        val hash: ByteArray = MessageDigest.getInstance("SHA-1").digest(key.toByteArray())
        val base64 = Base64Utils.encode(hash)
        writer.println("HTTP/1.1 101 Switching Protocols")
        writer.println("Upgrade: websocket")
        writer.println("Connection: Upgrade")
        writer.println("Sec-WebSocket-Accept: $base64")
        writer.println()
    }

    /**
     * Значение заголовка [Upgrade] используется для указания на переключение на другой протокол
     * если такого загловка нет, то переключение не требуется
     */
    private fun prepareHandshake(headers: Map<String, String>): HandshakeType {
        return when (headers["Upgrade"]) {
            "websocket" -> HandshakeType.WEB_SOCKET
            else -> HandshakeType.HTTP
        }
    }

    /**
     * @param reader [BufferedReader] Читаем HTTP-заголовки из возвращаем в виде
     * @return [Map] с заголовками, где ключ - имя заголовка, значение - значение заголовка
     *
     * Содержит заголовки:
     * [Upgrade: websocket] - указание серверу переключение на другой протокол
     * [Connection: Upgrade] - опции соединения; может содержать 1+ значений; Upgrade указывает что соединение нужно обновить на новый протокол
     * [Sec-WebSocket-key] - 16-byte строка в base64 формате сгенерированная клиентом
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
                return ServerSocket(port, 0, InetAddress.getByName(host))
            } catch (e: Exception) {
                logger("could not open port: [$port]")
            }
        }
        throw GameServerError("Open Server failed")
    }
}

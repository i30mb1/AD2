package n7.ad2.feature.games.xo.domain.internal.server

import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.util.Base64
import kotlin.random.Random

/** Рукопожатие websocket (RFC-6455#section-1.3) с обеих сторон */
internal object WebSocketHandshake {

    private const val GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"

    /** Клиент: просит перейти на websocket и читает ответ побайтно, чтобы не забрать фреймы, идущие следом */
    fun request(input: InputStream, output: OutputStream, host: String, port: Int) {
        val key = Base64.getEncoder().encodeToString(Random.nextBytes(16))
        val request = "GET / HTTP/1.1\r\nHost: $host:$port\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Key: $key\r\nSec-WebSocket-Version: 13\r\n\r\n"
        output.write(request.toByteArray())
        output.flush()
        val status = WebSocketFrames.readHttpHeaderLines(input).firstOrNull().orEmpty()
        check(" 101 " in status) { "Not a websocket server: $status" }
    }

    /**
     * Читает HTTP-запрос клиента и отвечает 101 Switching Protocols.
     * Чтобы доказать рукопожатие: значение [Sec-WebSocket-Key] склеивается с GUID, хэшируется SHA-1 и уходит в Base64
     * заголовком [Sec-WebSocket-Accept]. Без ключа (не websocket-клиент) - [IllegalStateException].
     */
    fun accept(input: InputStream, output: OutputStream) {
        val key = readHeaders(input)["Sec-WebSocket-Key"] ?: error("Not a websocket request")
        val accept = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((key + GUID).toByteArray()))
        val response = "HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: $accept\r\n\r\n"
        output.write(response.toByteArray())
        output.flush()
    }

    /**
     * Заголовки "name: value" до пустой строки; split(":", limit = 2) оставляет двоеточия внутри значения (RFC-822#section-3.2)
     */
    private fun readHeaders(input: InputStream): Map<String, String> = WebSocketFrames.readHttpHeaderLines(input)
        .mapNotNull { line ->
            val parts = line.split(":", limit = 2)
            if (parts.size < 2) null else parts[0].trim() to parts[1].trim()
        }
        .toMap()
}

package n7.ad2.feature.games.xo.domain.internal.server.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import n7.ad2.feature.games.xo.domain.ServerCreator
import n7.ad2.feature.games.xo.domain.internal.server.FrameType
import n7.ad2.feature.games.xo.domain.internal.server.WebSocketFrames
import n7.ad2.feature.games.xo.domain.internal.server.data.Message
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerState
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerStatus
import n7.ad2.feature.games.xo.domain.internal.server.getBytes
import n7.ad2.feature.games.xo.domain.internal.server.socket.ServerCreatorImpl
import n7.ad2.feature.games.xo.domain.model.SocketServerModel
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import java.util.Base64
import kotlin.experimental.xor

class WebsocketServerController(private val scope: CoroutineScope = CoroutineScope(Job() + newSingleThreadContext("SocketServerController"))) : ServerController {

    private val _state: MutableStateFlow<ServerState> = MutableStateFlow(ServerState())
    override val state: StateFlow<ServerState> = _state
    private val serverCreator: ServerCreator = ServerCreatorImpl()
    private lateinit var server: SocketServerModel
    private var socket: Socket? = null

    override fun start(name: String, ip: InetAddress, port: Int) {
        scope.launch {
            try {
                server = serverCreator.create(name, ip, port)
                _state.update { it.copy(status = ServerStatus.Waiting(server)) }
                socket = server.serverSocket.accept()
                _state.update { it.copy(status = ServerStatus.Connected(server)) }
                collectMessages(requireNotNull(socket))
            } catch (e: Exception) {
                if (::server.isInitialized && !server.serverSocket.isClosed) {
                    _state.update { it.copy(status = ServerStatus.Closed, messages = emptyList()) }
                }
            }
        }
    }

    private fun collectMessages(socket: Socket) {
        scope.launch {
            try {
                val inputStream = socket.getInputStream()
                val outputStream = socket.getOutputStream()
                val writer = PrintWriter(outputStream)

                val headers = readHeaders(inputStream)
                handshakeWebSocket(headers, writer)
                runWebSocketCommunication(inputStream)
                // клиент закрыл соединение (Close-фрейм или конец потока): сообщаем через статус
                stop()
            } catch (e: Exception) {
                stop()
            }
        }
    }

    override fun send(text: String) {
        val output = socket?.getOutputStream() ?: error("Server closed")
        WebSocketFrames.writeText(output, text, mask = false)
        _state.update { it.copy(messages = it.messages + Message.Me(text)) }
    }

    override fun stop() {
        scope.coroutineContext.cancelChildren()
        runCatching { socket?.close() }
        runCatching { if (::server.isInitialized) server.serverSocket.close() }
        socket = null
        _state.update { it.copy(status = ServerStatus.Closed, messages = emptyList()) }
    }

    private fun runWebSocketCommunication(input: InputStream) {
        while (true) {
            val receivedFrame = WebSocketFrames.read(input)
            when (receivedFrame) {
                FrameType.Close -> return
                is FrameType.Text -> {
                    _state.update { it.copy(messages = it.messages + Message.Other(receivedFrame.text)) }
                }
            }
        }
    }


    /**
     * Согласно RFC-6455#section-1.3 чтобы доказать рукопожатие нужно:
     * 1) взять значение заголовка [Sec-WebSocket-Key] обьединить его с 258EAFA5-E914-47DA-95CA-C5AB0DC85B11
     * 2) захэшировать SHA-1 алгоритмом полученную строку
     * 3) полученные двоичные данные закодировать Base64 кодировкой
     * 4) полученные данные отправить клиенту под заголовком [Sec-WebSocket-Accept]
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
        val key = headers["Sec-WebSocket-Key"]!! + guid
        val bytes = MessageDigest.getInstance("SHA-1").digest(key.toByteArray())
        val base64 = Base64.getEncoder().encode(bytes)
        writer.println("HTTP/1.1 101 Switching Protocols")
        writer.println("Upgrade: websocket")
        writer.println("Connection: Upgrade")
        writer.println("Sec-WebSocket-Accept: ${String(base64)}")
        writer.println()
        writer.flush()
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

    /**
     * Читает HTTP-заголовки из [reader] и возвращает их в виде мапы (ключ - имя, значение - значение)
     *
     * @param input поток сокета, из которого заголовки читаются побайтно (см. [WebSocketFrames.readHttpHeaderLines])
     * @return Мапа с заголовками, где ключ - имя заголовка, значение - значение заголовка
     *
     * Примечание: Функция использует метод split(":", limit = 2), где параметр [limit] ограничивает
     * разделение строки только двумя частями. Это предполагает, что строки имеют формат "name: value",
     * и мы разделяем их на две части, игнорируя остальные двоеточия, если они присутствуют в значении
     */
    private fun readHeaders(input: InputStream): MutableMap<String, String> {
        val headers = mutableMapOf<String, String>()
        for (line in WebSocketFrames.readHttpHeaderLines(input)) {
            val headerParts = line.split(":", limit = 2)
            if (headerParts.size >= 2) {
                val headerName = headerParts[0].trim()
                val headerValue = headerParts[1].trim()
                headers[headerName] = headerValue
            }
        }
        return headers
    }
}

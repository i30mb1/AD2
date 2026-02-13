package n7.ad2.xo.cli.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.ad2.xo.cli.model.Message
import n7.ad2.xo.cli.model.ServerState
import n7.ad2.xo.cli.model.ServerStatus
import n7.ad2.xo.cli.model.SimpleServer
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import java.util.Scanner

class WebSocketServerController : CliServerController {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val _state = MutableStateFlow(ServerState())
    override val state: StateFlow<ServerState> = _state

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var clientOutput: OutputStream? = null

    override suspend fun start(name: String, ip: InetAddress, port: Int): Unit = withContext(Dispatchers.IO) {
        try {
            serverSocket = ServerSocket(port, 0, ip)
            val actualPort = serverSocket!!.localPort
            val actualIp = serverSocket!!.inetAddress.hostAddress!!

            val server = SimpleServer(name, actualIp, actualPort)
            _state.update {
                it.copy(
                    status = ServerStatus.Waiting(server),
                    messages = it.messages + Message.Info("WebSocket server started"),
                )
            }

            // Ждем подключения клиента
            scope.launch {
                try {
                    clientSocket = serverSocket!!.accept()
                    clientOutput = clientSocket!!.getOutputStream()

                    // Выполняем WebSocket handshake
                    if (performHandshake(clientSocket!!)) {
                        _state.update {
                            it.copy(
                                status = ServerStatus.Connected(server),
                                messages = it.messages + Message.Info("WebSocket client connected"),
                            )
                        }

                        // Слушаем сообщения от клиента
                        listenForWebSocketMessages()
                    } else {
                        _state.update {
                            it.copy(messages = it.messages + Message.Info("WebSocket handshake failed"))
                        }
                    }
                } catch (e: Exception) {
                    if (serverSocket?.isClosed == false) {
                        _state.update {
                            it.copy(
                                status = ServerStatus.Closed,
                                messages = it.messages + Message.Info("WebSocket server error: ${e.message}"),
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    status = ServerStatus.Closed,
                    messages = it.messages + Message.Info("Failed to start WebSocket server: ${e.message}"),
                )
            }
        }
    }

    private suspend fun performHandshake(socket: Socket): Boolean = withContext(Dispatchers.IO) {
        try {
            val input = socket.getInputStream()
            val output = socket.getOutputStream()

            // Читаем HTTP запрос
            val scanner = Scanner(input)
            val headers = mutableMapOf<String, String>()
            var isFirstLine = true

            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()
                if (line.isEmpty()) break

                if (isFirstLine) {
                    isFirstLine = false
                    continue
                }

                val parts = line.split(": ", limit = 2)
                if (parts.size == 2) {
                    headers[parts[0]] = parts[1]
                }
            }

            val websocketKey = headers["Sec-WebSocket-Key"] ?: return@withContext false

            // Генерируем ответный ключ
            val acceptKey = generateAcceptKey(websocketKey)

            // Отправляем ответ
            val response = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: $acceptKey\r\n" +
                "\r\n"

            output.write(response.toByteArray())
            output.flush()

            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun listenForWebSocketMessages() = withContext(Dispatchers.IO) {
        try {
            val input = clientSocket!!.getInputStream()

            while (clientSocket?.isConnected == true && !clientSocket!!.isClosed) {
                val frame = readWebSocketFrame(input)
                if (frame != null) {
                    when (frame.opcode) {
                        0x1 -> { // Text frame
                            val message = String(frame.payload, StandardCharsets.UTF_8)
                            _state.update {
                                it.copy(messages = it.messages + Message.Other("WS Client: $message"))
                            }
                            println("📨 WebSocket Received: $message")
                        }

                        0x8 -> { // Close frame
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(messages = it.messages + Message.Info("WebSocket connection lost: ${e.message}"))
            }
        }
    }

    override suspend fun send(text: String) = withContext(Dispatchers.IO) {
        try {
            if (clientOutput != null && clientSocket?.isConnected == true) {
                val frame = createWebSocketFrame(text.toByteArray(StandardCharsets.UTF_8))
                clientOutput!!.write(frame)
                clientOutput!!.flush()

                _state.update {
                    it.copy(messages = it.messages + Message.Me("WS Server: $text"))
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(messages = it.messages + Message.Info("Failed to send WebSocket message: ${e.message}"))
            }
        }
    }

    private fun generateAcceptKey(websocketKey: String): String {
        val concat = websocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
        val sha1 = MessageDigest.getInstance("SHA-1")
        val digest = sha1.digest(concat.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(digest)
    }

    private data class WebSocketFrame(val opcode: Int, val payload: ByteArray)

    private fun readWebSocketFrame(input: InputStream): WebSocketFrame? {
        return try {
            val firstByte = input.read()
            if (firstByte == -1) return null

            val opcode = firstByte and 0x0F

            val secondByte = input.read()
            if (secondByte == -1) return null

            val masked = (secondByte and 0x80) != 0
            var payloadLength = (secondByte and 0x7F).toLong()

            when (payloadLength) {
                126L -> {
                    val len1 = input.read()
                    val len2 = input.read()
                    payloadLength = ((len1 shl 8) or len2).toLong()
                }

                127L -> {
                    // Extended payload length (8 bytes) - simplified
                    for (i in 0 until 8) {
                        input.read()
                    }
                    payloadLength = 0 // Simplified - not handling large frames
                }
            }

            val mask = if (masked) {
                ByteArray(4) { input.read().toByte() }
            } else {
                null
            }

            val payload = ByteArray(payloadLength.toInt())
            input.read(payload)

            if (masked && mask != null) {
                for (i in payload.indices) {
                    payload[i] = (payload[i].toInt() xor mask[i % 4].toInt()).toByte()
                }
            }

            WebSocketFrame(opcode, payload)
        } catch (e: Exception) {
            null
        }
    }

    private fun createWebSocketFrame(payload: ByteArray): ByteArray {
        val payloadLength = payload.size
        val frameSize = when {
            payloadLength < 126 -> 2 + payloadLength
            payloadLength < 65536 -> 4 + payloadLength
            else -> 10 + payloadLength
        }

        val frame = ByteArray(frameSize)
        var offset = 0

        // First byte: FIN=1, RSV=000, opcode=0001 (text)
        frame[offset++] = 0x81.toByte()

        // Payload length
        when {
            payloadLength < 126 -> {
                frame[offset++] = payloadLength.toByte()
            }

            payloadLength < 65536 -> {
                frame[offset++] = 126.toByte()
                frame[offset++] = (payloadLength shr 8).toByte()
                frame[offset++] = (payloadLength and 0xFF).toByte()
            }

            else -> {
                frame[offset++] = 127.toByte()
                for (i in 7 downTo 0) {
                    frame[offset++] = (payloadLength shr (i * 8)).toByte()
                }
            }
        }

        // Payload
        System.arraycopy(payload, 0, frame, offset, payloadLength)

        return frame
    }

    override fun stop() {
        scope.cancel()
        runCatching { clientSocket?.close() }
        runCatching { serverSocket?.close() }
        _state.update { it.copy(status = ServerStatus.Closed, messages = emptyList()) }
    }
}

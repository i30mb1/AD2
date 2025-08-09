package n7.ad2.xo.cli.controller

import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import java.util.Scanner
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.ad2.xo.cli.model.ClientState
import n7.ad2.xo.cli.model.ClientStatus
import n7.ad2.xo.cli.model.Message
import n7.ad2.xo.cli.model.SimpleServer

class WebSocketClientController : CliClientController {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val _state = MutableStateFlow(ClientState())
    override val state: StateFlow<ClientState> = _state

    private var socket: Socket? = null
    private var output: OutputStream? = null

    override suspend fun connect(name: String, ip: InetAddress, port: Int): Unit = withContext(Dispatchers.IO) {
        try {
            socket = Socket(ip, port)
            output = socket!!.getOutputStream()

            // Выполняем WebSocket handshake
            if (performHandshake(socket!!)) {
                val server = SimpleServer(name, ip.hostAddress!!, port)
                _state.update {
                    it.copy(
                        status = ClientStatus.Connected(server),
                        messages = it.messages + Message.Info("Connected to WebSocket server")
                    )
                }

                // Слушаем сообщения от сервера
                scope.launch {
                    listenForWebSocketMessages()
                }
            } else {
                _state.update {
                    it.copy(
                        status = ClientStatus.Disconnected,
                        messages = it.messages + Message.Info("WebSocket handshake failed")
                    )
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    status = ClientStatus.Disconnected,
                    messages = it.messages + Message.Info("Failed to connect to WebSocket server: ${e.message}")
                )
            }
        }
    }

    private suspend fun performHandshake(socket: Socket): Boolean = withContext(Dispatchers.IO) {
        try {
            val input = socket.getInputStream()
            val output = socket.getOutputStream()

            // Генерируем WebSocket ключ
            val websocketKey = generateWebSocketKey()

            // Формируем HTTP запрос для handshake
            val request = "GET / HTTP/1.1\r\n" +
                    "Host: ${socket.inetAddress.hostAddress}:${socket.port}\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Sec-WebSocket-Key: $websocketKey\r\n" +
                    "Sec-WebSocket-Version: 13\r\n" +
                    "\r\n"

            output.write(request.toByteArray())
            output.flush()

            // Читаем ответ
            val scanner = Scanner(input)
            val firstLine = scanner.nextLine()

            if (!firstLine.contains("101 Switching Protocols")) {
                return@withContext false
            }

            val headers = mutableMapOf<String, String>()
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()
                if (line.isEmpty()) break

                val parts = line.split(": ", limit = 2)
                if (parts.size == 2) {
                    headers[parts[0]] = parts[1]
                }
            }

            // Проверяем ответный ключ
            val expectedAcceptKey = generateAcceptKey(websocketKey)
            val actualAcceptKey = headers["Sec-WebSocket-Accept"]

            expectedAcceptKey == actualAcceptKey
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun listenForWebSocketMessages() = withContext(Dispatchers.IO) {
        try {
            val input = socket!!.getInputStream()

            while (socket?.isConnected == true && !socket!!.isClosed) {
                val frame = readWebSocketFrame(input)
                if (frame != null) {
                    when (frame.opcode) {
                        0x1 -> { // Text frame
                            val message = String(frame.payload, StandardCharsets.UTF_8)
                            _state.update {
                                it.copy(messages = it.messages + Message.Other("WS Server: $message"))
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
                it.copy(
                    status = ClientStatus.Disconnected,
                    messages = it.messages + Message.Info("WebSocket connection lost: ${e.message}")
                )
            }
        }
    }

    override suspend fun send(text: String) = withContext(Dispatchers.IO) {
        try {
            if (output != null && socket?.isConnected == true) {
                val frame = createWebSocketFrame(text.toByteArray(StandardCharsets.UTF_8), masked = true)
                output!!.write(frame)
                output!!.flush()

                _state.update {
                    it.copy(messages = it.messages + Message.Me("WS Client: $text"))
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(messages = it.messages + Message.Info("Failed to send WebSocket message: ${e.message}"))
            }
        }
    }

    private fun generateWebSocketKey(): String {
        val keyBytes = ByteArray(16)
        Random.nextBytes(keyBytes)
        return Base64.getEncoder().encodeToString(keyBytes)
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

            val fin = (firstByte and 0x80) != 0
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
            } else null

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

    private fun createWebSocketFrame(payload: ByteArray, masked: Boolean = false): ByteArray {
        val payloadLength = payload.size
        val maskSize = if (masked) 4 else 0
        val frameSize = when {
            payloadLength < 126 -> 2 + maskSize + payloadLength
            payloadLength < 65536 -> 4 + maskSize + payloadLength
            else -> 10 + maskSize + payloadLength
        }

        val frame = ByteArray(frameSize)
        var offset = 0

        // First byte: FIN=1, RSV=000, opcode=0001 (text)
        frame[offset++] = 0x81.toByte()

        // Payload length and mask bit
        val maskBit = if (masked) 0x80 else 0x00
        when {
            payloadLength < 126 -> {
                frame[offset++] = (payloadLength or maskBit).toByte()
            }

            payloadLength < 65536 -> {
                frame[offset++] = (126 or maskBit).toByte()
                frame[offset++] = (payloadLength shr 8).toByte()
                frame[offset++] = (payloadLength and 0xFF).toByte()
            }

            else -> {
                frame[offset++] = (127 or maskBit).toByte()
                for (i in 7 downTo 0) {
                    frame[offset++] = (payloadLength shr (i * 8)).toByte()
                }
            }
        }

        // Masking key (client должен маскировать данные)
        val mask = if (masked) {
            val maskBytes = ByteArray(4)
            Random.nextBytes(maskBytes)
            System.arraycopy(maskBytes, 0, frame, offset, 4)
            offset += 4
            maskBytes
        } else null

        // Payload (masked if needed)
        if (masked && mask != null) {
            for (i in payload.indices) {
                frame[offset + i] = (payload[i].toInt() xor mask[i % 4].toInt()).toByte()
            }
        } else {
            System.arraycopy(payload, 0, frame, offset, payloadLength)
        }

        return frame
    }

    override fun disconnect() {
        scope.cancel()
        runCatching { socket?.close() }
        _state.update { it.copy(status = ClientStatus.Disconnected, messages = emptyList()) }
    }
}

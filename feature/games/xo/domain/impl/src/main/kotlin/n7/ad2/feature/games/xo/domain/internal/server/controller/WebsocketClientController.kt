package n7.ad2.feature.games.xo.domain.internal.server.controller

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
import java.util.UUID
import kotlin.experimental.xor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import n7.ad2.feature.games.xo.domain.internal.server.FrameType
import n7.ad2.feature.games.xo.domain.internal.server.OpCode
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientState
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientStatus
import n7.ad2.feature.games.xo.domain.internal.server.data.Message
import n7.ad2.feature.games.xo.domain.internal.server.getBytes
import n7.ad2.feature.games.xo.domain.internal.server.socket.ClientCreatorImpl
import n7.ad2.feature.games.xo.domain.model.SimpleServer

class WebsocketClientController(
    private val scope: CoroutineScope = CoroutineScope(Job() + newSingleThreadContext("WebsocketClientController")),
) : ClientController {
    
    private val _state = MutableStateFlow(ClientState())
    override val state: StateFlow<ClientState> = _state
    private var socket: Socket? = null
    private val clientCreator = ClientCreatorImpl()

    override fun connect(name: String, ip: InetAddress, port: Int) {
        scope.launch {
            if (socket != null) error("Already connected")
            socket = clientCreator.create(ip, port)
            val server = SimpleServer(name, ip.hostAddress!!, port)
            _state.update { it.copy(status = ClientStatus.Connected(server)) }
            performWebSocketHandshake(requireNotNull(socket))
            collectMessages(requireNotNull(socket))
        }
    }

    private fun performWebSocketHandshake(socket: Socket) {
        val outputStream = socket.getOutputStream()
        val inputStream = socket.getInputStream()
        val writer = PrintWriter(outputStream)
        val reader = BufferedReader(InputStreamReader(inputStream))

        // Генерируем случайный ключ для WebSocket handshake
        val webSocketKey = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().toByteArray())
        
        // Отправляем HTTP запрос для upgrade до WebSocket
        writer.println("GET / HTTP/1.1")
        writer.println("Host: ${socket.inetAddress.hostAddress}:${socket.port}")
        writer.println("Upgrade: websocket")
        writer.println("Connection: Upgrade")
        writer.println("Sec-WebSocket-Key: $webSocketKey")
        writer.println("Sec-WebSocket-Version: 13")
        writer.println()
        writer.flush()

        // Читаем ответ сервера
        var line = reader.readLine()
        while (line.isNotEmpty()) {
            line = reader.readLine()
        }
        
        // В реальном приложении здесь нужно проверить Sec-WebSocket-Accept заголовок
        _state.update { it.copy(messages = it.messages + Message.Info("WebSocket handshake completed")) }
    }

    private suspend fun collectMessages(socket: Socket) = coroutineScope {
        val inputStream = socket.getInputStream()
        try {
            while (true) {
                ensureActive()
                val receivedFrame = readWebSocketFrame(inputStream)
                when (receivedFrame) {
                    FrameType.Close -> {
                        disconnect()
                        break
                    }
                    is FrameType.Text -> {
                        val message = Message.Other(receivedFrame.text)
                        _state.update { it.copy(messages = it.messages + message) }
                    }
                }
            }
        } catch (e: Exception) {
            disconnect()
        }
    }

    override fun send(text: String) {
        val socket = socket ?: error("Not connected")
        val outputStream = socket.getOutputStream()
        sendWebSocketFrame(outputStream, text)
        val message = Message.Me(text)
        _state.update { it.copy(messages = it.messages + message) }
    }

    override fun disconnect() {
        scope.coroutineContext.cancelChildren()
        runCatching { socket?.close() }
        socket = null
        _state.update { it.copy(status = ClientStatus.Disconnected, messages = emptyList()) }
    }

    private fun sendWebSocketFrame(output: OutputStream, text: String) {
        val utf8Bytes = text.toByteArray()
        val maskKey = ByteArray(4) { (Math.random() * 256).toInt().toByte() }
        
        // Маскируем данные (клиент всегда должен маскировать)
        val maskedPayload = ByteArray(utf8Bytes.size)
        for (i in utf8Bytes.indices) {
            maskedPayload[i] = utf8Bytes[i] xor maskKey[i % 4]
        }
        
        // Первый байт: FIN=1, RSV=000, opcode=0001 (text)
        output.write(0b10000001)
        // Второй байт: MASK=1, payload length
        output.write(0b10000000 or utf8Bytes.size)
        // Ключ маскировки
        output.write(maskKey)
        // Замаскированные данные
        output.write(maskedPayload)
        output.flush()
    }

    private fun readWebSocketFrame(input: InputStream): FrameType {
        val b1 = input.read()
        val isFinalFrame = b1 and 0b10000000
        val opCode = b1 and 0b00001111
        
        when (opCode) {
            OpCode.OPCODE_TEXT -> Unit
            OpCode.OPCODE_CLOSE -> return FrameType.Close
            else -> return FrameType.Close
        }

        val b2 = input.read()
        val isMasked = b2 and 0b10000000 != 0
        var frameLength = (b2 and 0b01111111).toLong()
        
        when (frameLength) {
            126L -> {
                val bytes = input.getBytes(2)
                frameLength = (bytes[0].toLong() shl 8) or bytes[1].toLong()
            }
            127L -> {
                val bytes = input.getBytes(8)
                frameLength = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).long
            }
        }

        val maskKey = if (isMasked) input.getBytes(4) else ByteArray(0)
        val payload = input.getBytes(frameLength)
        
        if (isMasked) {
            for (i in payload.indices) {
                payload[i] = payload[i] xor maskKey[i % 4]
            }
        }

        return FrameType.Text(String(payload))
    }
}
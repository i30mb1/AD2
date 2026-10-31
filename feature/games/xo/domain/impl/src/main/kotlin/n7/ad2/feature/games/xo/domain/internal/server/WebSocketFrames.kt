package n7.ad2.feature.games.xo.domain.internal.server

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.xor
import kotlin.random.Random

/**
 * Кодирование текстовых фреймов RFC-6455#section-5.2: длина до 125 байт - в 7 битах, до 65535 - 126 + 2 байта, больше - 127 + 8 байт
 */
internal object WebSocketFrames {

    private const val FIN_TEXT = 0b10000001
    private const val MASK_BIT = 0b10000000
    private const val LENGTH_16 = 126
    private const val LENGTH_64 = 127

    /**
     * @param mask клиент обязан маскировать фреймы, сервер - нет (RFC-6455#section-5.3)
     */
    fun writeText(output: OutputStream, text: String, mask: Boolean) {
        val payload = text.toByteArray()
        val maskBit = if (mask) MASK_BIT else 0
        output.write(FIN_TEXT)
        when {
            payload.size < LENGTH_16 -> output.write(maskBit or payload.size)
            payload.size <= 0xFFFF -> {
                output.write(maskBit or LENGTH_16)
                output.write(payload.size shr 8 and 0xFF)
                output.write(payload.size and 0xFF)
            }
            else -> {
                output.write(maskBit or LENGTH_64)
                output.write(ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(payload.size.toLong()).array())
            }
        }
        if (mask) {
            val maskKey = Random.nextBytes(4)
            output.write(maskKey)
            output.write(ByteArray(payload.size) { index -> payload[index] xor maskKey[index % 4] })
        } else {
            output.write(payload)
        }
        output.flush()
    }

    fun read(input: InputStream): FrameType {
        val b1 = input.read()
        if (b1 == -1) return FrameType.Close
        when (b1 and 0b00001111) {
            OpCode.OPCODE_TEXT -> Unit
            else -> return FrameType.Close
        }
        val b2 = input.read()
        if (b2 == -1) return FrameType.Close
        val isMasked = b2 and MASK_BIT != 0
        val length = when (val shortLength = b2 and 0b01111111) {
            LENGTH_16 -> input.readFully(2).let { (it[0].toInt() and 0xFF shl 8) or (it[1].toInt() and 0xFF) }.toLong()
            LENGTH_64 -> ByteBuffer.wrap(input.readFully(8)).order(ByteOrder.BIG_ENDIAN).long
            else -> shortLength.toLong()
        }
        val maskKey = if (isMasked) input.readFully(4) else null
        val payload = input.readFully(length.toInt())
        if (maskKey != null) {
            for (index in payload.indices) payload[index] = payload[index] xor maskKey[index % 4]
        }
        return FrameType.Text(String(payload))
    }

    /**
     * Читает строки HTTP-заголовков до пустой строки побайтно, чтобы не забрать из потока фреймы, идущие следом
     * (BufferedReader читает с опережением и может проглотить первый фрейм)
     */
    fun readHttpHeaderLines(input: InputStream): List<String> {
        val lines = mutableListOf<String>()
        val line = ByteArrayOutputStream()
        while (true) {
            val byte = input.read()
            if (byte == -1) error("Stream closed before end of HTTP headers")
            if (byte == '\n'.code) {
                val text = line.toString(Charsets.UTF_8.name()).trimEnd('\r')
                if (text.isEmpty()) return lines
                lines += text
                line.reset()
            } else {
                line.write(byte)
            }
        }
    }
}

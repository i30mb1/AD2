import com.google.common.truth.Truth
import n7.ad2.feature.games.xo.domain.internal.server.FrameType
import n7.ad2.feature.games.xo.domain.internal.server.WebSocketFrames
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FilterInputStream
import java.io.InputStream

internal class WebSocketFramesTest {

    private fun roundTrip(text: String, mask: Boolean): FrameType {
        val output = ByteArrayOutputStream()
        WebSocketFrames.writeText(output, text, mask)
        return WebSocketFrames.read(ByteArrayInputStream(output.toByteArray()))
    }

    @Test
    fun `text of any length survives masked and unmasked`() {
        for (length in listOf(0, 1, 125, 126, 300, 65535, 70000)) {
            for (mask in listOf(true, false)) {
                val text = "a".repeat(length)
                Truth.assertWithMessage("length=$length mask=$mask").that(roundTrip(text, mask)).isEqualTo(FrameType.Text(text))
            }
        }
    }

    @Test
    fun `cyrillic text survives`() {
        val text = "привет".repeat(30)

        Truth.assertThat(roundTrip(text, mask = true)).isEqualTo(FrameType.Text(text))
    }

    @Test
    fun `length 126 frame header is 0x7E followed by two bytes`() {
        val output = ByteArrayOutputStream()
        WebSocketFrames.writeText(output, "a".repeat(126), mask = false)
        val bytes = output.toByteArray()

        Truth.assertThat(bytes[1].toInt()).isEqualTo(126)
        Truth.assertThat(bytes[2].toInt()).isEqualTo(0x00)
        Truth.assertThat(bytes[3].toInt()).isEqualTo(0x7E)
    }

    @Test
    fun `payload arriving in small chunks is read completely`() {
        val output = ByteArrayOutputStream()
        val text = "b".repeat(5000)
        WebSocketFrames.writeText(output, text, mask = true)
        val chunked = object : FilterInputStream(ByteArrayInputStream(output.toByteArray())) {
            override fun read(b: ByteArray, off: Int, len: Int): Int = super.read(b, off, minOf(len, 7))
        }

        Truth.assertThat(WebSocketFrames.read(chunked)).isEqualTo(FrameType.Text(text))
    }

    @Test
    fun `empty stream reads as Close`() {
        Truth.assertThat(WebSocketFrames.read(ByteArrayInputStream(ByteArray(0)))).isEqualTo(FrameType.Close)
    }

    @Test
    fun `http headers are read without consuming the frame that follows`() {
        val output = ByteArrayOutputStream()
        output.write("GET / HTTP/1.1\r\nUpgrade: websocket\r\nSec-WebSocket-Key: abc\r\n\r\n".toByteArray())
        WebSocketFrames.writeText(output, "hello", mask = true)
        val input: InputStream = ByteArrayInputStream(output.toByteArray())

        val lines = WebSocketFrames.readHttpHeaderLines(input)

        Truth.assertThat(lines).containsExactly("GET / HTTP/1.1", "Upgrade: websocket", "Sec-WebSocket-Key: abc").inOrder()
        Truth.assertThat(WebSocketFrames.read(input)).isEqualTo(FrameType.Text("hello"))
    }
}

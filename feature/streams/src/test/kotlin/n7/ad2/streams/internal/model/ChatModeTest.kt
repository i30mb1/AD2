package n7.ad2.streams.internal.model

import n7.ad2.streams.internal.chat.ChatMessage
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatModeTest {

    // before the panel the chat had a density: off, low, normal, high, all but off flew over the video
    @Test
    fun `the saved density of older versions becomes a mode`() {
        assertEquals(ChatMode.OFF, ChatMode.of("OFF"))
        assertEquals(listOf(ChatMode.TICKER, ChatMode.TICKER, ChatMode.TICKER), listOf("LOW", "NORMAL", "HIGH").map(ChatMode::of))
        assertEquals(ChatMode.PANEL, ChatMode.of("PANEL"))
        assertEquals(ChatMode.TICKER, ChatMode.of(null))
    }

    @Test
    fun `the panel keeps the last messages, each with its own id`() {
        var lines = emptyList<ChatLine>()
        repeat(CHAT_LOG_SIZE + 5) { index -> lines = lines.append(ChatMessage("a", "message $index", null)) }

        assertEquals(CHAT_LOG_SIZE, lines.size)
        assertEquals("message 5", lines.first().message.text)
        assertEquals("message ${CHAT_LOG_SIZE + 4}", lines.last().message.text)
        assertEquals(lines.size, lines.map { it.id }.toSet().size)
    }
}

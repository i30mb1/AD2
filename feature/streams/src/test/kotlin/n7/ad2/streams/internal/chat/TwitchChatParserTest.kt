package n7.ad2.streams.internal.chat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TwitchChatParserTest {

    @Test
    fun `message with tags gives author, text and color`() {
        val line = "@badge-info=;color=#FF4500;display-name=Vova228;emotes= :vova228!vova228@vova228.tmi.twitch.tv PRIVMSG #dota2ti_ru :спирит в финале 100%\r\n"
        assertEquals(ChatMessage("Vova228", "спирит в финале 100%", 0xFFFF4500.toInt()), TwitchChatParser.parse(line))
    }

    @Test
    fun `text keeps the colons after the first one`() {
        val line = "@color=;display-name=kek :kek!kek@kek.tmi.twitch.tv PRIVMSG #ch :score: 2:1"
        assertEquals(ChatMessage("kek", "score: 2:1", null), TwitchChatParser.parse(line))
    }

    @Test
    fun `without display name the nick is the author`() {
        val line = ":petya!petya@petya.tmi.twitch.tv PRIVMSG #ch :где бкб"
        assertEquals(ChatMessage("petya", "где бкб", null), TwitchChatParser.parse(line))
    }

    @Test
    fun `me action is unwrapped`() {
        val line = "@display-name=Ana :ana!ana@ana.tmi.twitch.tv PRIVMSG #ch :\u0001ACTION waves\u0001"
        assertEquals("waves", TwitchChatParser.parse(line)?.text)
    }

    @Test
    fun `service lines are skipped`() {
        assertNull(TwitchChatParser.parse(":tmi.twitch.tv 001 justinfan123 :Welcome, GLHF!"))
        assertNull(TwitchChatParser.parse(":justinfan123!justinfan123@justinfan123.tmi.twitch.tv JOIN #ch"))
        assertNull(TwitchChatParser.parse("@msg-id=subs_on :tmi.twitch.tv NOTICE #ch :This room is now in subscribers-only mode."))
    }
}

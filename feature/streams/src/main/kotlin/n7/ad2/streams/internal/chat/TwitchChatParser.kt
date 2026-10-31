package n7.ad2.streams.internal.chat

private const val PRIVMSG = " PRIVMSG #"
private const val ACTION_START = "\u0001ACTION "
private const val ACTION_END = "\u0001"
private const val HEX_COLOR_LENGTH = 7
private const val OPAQUE = 0xFF000000.toInt()

/**
 * Reads one IRC line of the Twitch chat, for example
 * `@color=#FF4500;display-name=Vova;... :vova!vova@vova.tmi.twitch.tv PRIVMSG #channel :hello`.
 * Everything that is not a chat message (joins, notices, pings) gives null.
 */
internal object TwitchChatParser {

    fun parse(line: String): ChatMessage? {
        var rest = line.trimEnd('\r', '\n')
        var tags = emptyMap<String, String>()
        if (rest.startsWith("@")) {
            val end = rest.indexOf(' ')
            if (end < 0) return null
            tags = parseTags(rest.substring(1, end))
            rest = rest.substring(end + 1)
        }
        val command = rest.indexOf(PRIVMSG)
        if (command < 0) return null
        val textStart = rest.indexOf(" :", command + PRIVMSG.length)
        if (textStart < 0) return null
        var text = rest.substring(textStart + 2)
        // "/me waves" comes wrapped in a CTCP ACTION
        if (text.startsWith(ACTION_START)) text = text.removePrefix(ACTION_START).removeSuffix(ACTION_END)
        if (text.isBlank()) return null

        val nick = rest.substringAfter(':', "").substringBefore('!')
        val author = tags["display-name"].orEmpty().ifEmpty { nick }
        return ChatMessage(author, text, parseColor(tags["color"]))
    }

    private fun parseTags(raw: String): Map<String, String> = raw.split(';').associate { tag ->
        tag.substringBefore('=') to tag.substringAfter('=', "")
    }

    private fun parseColor(hex: String?): Int? {
        if (hex == null || hex.length != HEX_COLOR_LENGTH || !hex.startsWith("#")) return null
        return hex.substring(1).toIntOrNull(radix = 16)?.let { rgb -> rgb or OPAQUE }
    }
}

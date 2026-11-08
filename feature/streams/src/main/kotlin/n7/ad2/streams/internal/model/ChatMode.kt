package n7.ad2.streams.internal.model

import n7.ad2.streams.internal.chat.ChatMessage

/** How the chat goes with the stream: flying over the video, in a panel beside it that makes the video smaller, or not at all. */
internal enum class ChatMode {
    TICKER,
    PANEL,
    OFF,
    ;

    companion object {
        /** [saved] may be a density of the older versions (LOW, NORMAL, HIGH), all of them flew over the video. */
        fun of(saved: String?): ChatMode = entries.find { it.name == saved } ?: TICKER
    }
}

internal const val CHAT_LOG_SIZE = 150

/** A message of the panel, [id] tells it apart from the same text written again. */
internal data class ChatLine(val id: Long, val message: ChatMessage)

/** The panel shows the last [CHAT_LOG_SIZE] messages, the oldest go first. */
internal fun List<ChatLine>.append(message: ChatMessage): List<ChatLine> =
    (this + ChatLine((lastOrNull()?.id ?: 0) + 1, message)).takeLast(CHAT_LOG_SIZE)

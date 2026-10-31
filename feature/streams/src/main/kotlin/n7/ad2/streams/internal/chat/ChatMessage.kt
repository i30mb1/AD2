package n7.ad2.streams.internal.chat

/** [color] is the ARGB color the author picked on Twitch, null when they did not pick one. */
internal data class ChatMessage(val author: String, val text: String, val color: Int?)

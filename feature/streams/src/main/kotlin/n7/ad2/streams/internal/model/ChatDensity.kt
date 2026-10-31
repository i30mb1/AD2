package n7.ad2.streams.internal.model

/** How much of the chat flies over the video: [lanes] rows of messages at the top of the screen. */
internal enum class ChatDensity(val lanes: Int) {
    OFF(0),
    LOW(2),
    NORMAL(3),
    HIGH(4),
}

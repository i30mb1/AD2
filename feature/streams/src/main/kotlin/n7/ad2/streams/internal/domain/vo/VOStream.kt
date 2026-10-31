package n7.ad2.streams.internal.domain.vo

internal sealed class VOStream {
    /**
     * [streamerName] is the login used in urls and the chat, [displayName] is how the streamer writes it.
     * [startedAtMillis] is 0 when Twitch did not say when the stream started.
     */
    data class Simple(
        val title: String,
        val streamerName: String,
        val imageUrl: String,
        val viewers: Int = 0,
        val displayName: String = streamerName,
        val language: String = "",
        val startedAtMillis: Long = 0,
    ) : VOStream()
}

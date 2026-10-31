package n7.ad2.streams.internal.model

/** Filter of the streams list, [code] is the Twitch language code, null for every language. */
internal enum class StreamsLanguage(val code: String?) {
    ALL(null),
    RUSSIAN("ru"),
    ENGLISH("en"),
}

package n7.ad2.streams.internal.domain.vo

internal sealed class VOStream {
    data class Simple(val title: String, val streamerName: String, val imageUrl: String) : VOStream()
}

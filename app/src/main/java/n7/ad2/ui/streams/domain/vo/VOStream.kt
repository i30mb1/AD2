package n7.ad2.ui.streams.domain.vo

sealed class VOStream
data class VOSimpleStream(
    val title: String,
    val imageUrl: String,
) : VOStream()
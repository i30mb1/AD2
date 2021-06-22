package n7.ad2.data.source.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Streams(
    @Json(name = "_total")
    val _total: Int = 0,
    @Json(name = "streams")
    val streams: List<Stream> = emptyList(),
)

class Stream(
    @Json(name = "viewers")
    val viewers: Int = 0,
    @Json(name = "preview")
    val preview: Preview? = null,
    @Json(name = "channel")
    val channel: Channel = Channel(),
    val isPremium: Boolean = false,
)

class Preview(
    @Json(name = "medium")
    val medium: String = "",
)

class Channel(
    @Json(name = "status")
    val status: String = "",
    @Json(name = "display_name")
    val name: String = "",
)

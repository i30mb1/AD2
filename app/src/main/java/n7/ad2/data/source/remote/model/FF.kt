package n7.ad2.data.source.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class FF(
    @Json(name = "data")
    val `data`: Data = Data(),
    @Json(name = "extensions")
    val extensions: Extensions1 = Extensions1(),
)

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "streamPlaybackAccessToken")
    val streamPlaybackAccessToken: StreamPlaybackAccessToken = StreamPlaybackAccessToken(),
)

@JsonClass(generateAdapter = true)
data class Extensions1(
    @Json(name = "durationMilliseconds")
    val durationMilliseconds: Int = 0,
    @Json(name = "operationName")
    val operationName: String = "",
    @Json(name = "requestID")
    val requestID: String = "",
)

@JsonClass(generateAdapter = true)
data class StreamPlaybackAccessToken(
    @Json(name = "signature")
    val signature: String = "",
    @Json(name = "__typename")
    val typename: String = "",
    @Json(name = "value")
    val value: String = "",
)
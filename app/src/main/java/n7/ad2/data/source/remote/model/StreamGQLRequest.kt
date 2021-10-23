package n7.ad2.data.source.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class StreamGQLRequest(
    @Json(name = "extensions")
    val extensions: Extensions = Extensions(),
    @Json(name = "operationName")
    val operationName: String = "PlaybackAccessToken",
    @Json(name = "variables")
    val variables: Variables,
)

@JsonClass(generateAdapter = true)
data class Extensions(
    @Json(name = "persistedQuery")
    val persistedQuery: PersistedQuery = PersistedQuery(),
)

@JsonClass(generateAdapter = true)
data class Variables(
    @Json(name = "isLive")
    val isLive: Boolean = true,
    @Json(name = "isVod")
    val isVod: Boolean = false,
    @Json(name = "login")
    val streamerName: String,
    @Json(name = "playerType")
    val playerType: String = "embed",
    @Json(name = "vodID")
    val vodID: String = "",
)

@JsonClass(generateAdapter = true)
data class PersistedQuery(
    @Json(name = "sha256Hash")
    val sha256Hash: String = "0828119ded1c13477966434e15800ff57ddacf13ba1911c129dc2200705b0712",
    @Json(name = "version")
    val version: Int = 1,
)
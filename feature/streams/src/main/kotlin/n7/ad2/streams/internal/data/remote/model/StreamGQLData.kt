package n7.ad2.streams.internal.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamGQLData(
    @SerialName("data")
    val `data`: Data = Data(),
    @SerialName("extensions")
    val extensions: Extensions1 = Extensions1(),
)

@Serializable
data class Data(
    @SerialName("streamPlaybackAccessToken")
    val streamPlaybackAccessToken: StreamPlaybackAccessToken = StreamPlaybackAccessToken(),
)

@Serializable
data class Extensions1(
    @SerialName("durationMilliseconds")
    val durationMilliseconds: Int = 0,
    @SerialName("operationName")
    val operationName: String = "",
    @SerialName("requestID")
    val requestID: String = "",
)

@Serializable
data class StreamPlaybackAccessToken(
    @SerialName("signature")
    val signature: String = "",
    @SerialName("__typename")
    val typename: String = "",
    @SerialName("value")
    val value: String = "",
)

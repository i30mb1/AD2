package n7.ad2.streams.internal.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class StreamGQLRequest(
    @SerialName("extensions")
    val extensions: Extensions = Extensions(),
    @SerialName("operationName")
    val operationName: String = "PlaybackAccessToken",
    @SerialName("variables")
    val variables: Variables,
)

@Serializable
data class Extensions(
    @SerialName("persistedQuery")
    val persistedQuery: PersistedQuery = PersistedQuery(),
)

@Serializable
data class Variables(
    @SerialName("isLive")
    val isLive: Boolean = true,
    @SerialName("isVod")
    val isVod: Boolean = false,
    @SerialName("login")
    val streamerName: String,
    @SerialName("playerType")
    val playerType: String = "embed",
    @SerialName("vodID")
    val vodID: String = "",
)

@Serializable
data class PersistedQuery(
    @SerialName("sha256Hash")
    val sha256Hash: String = "0828119ded1c13477966434e15800ff57ddacf13ba1911c129dc2200705b0712",
    @SerialName("version")
    val version: Int = 1,
)
package n7.ad2.streams.internal.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamGQL(
    @SerialName("stream")
    val stream: StreamInfo = StreamInfo(),
)

@Serializable
data class StreamInfo(
    @SerialName("average_fps")
    val averageFps: Int = 0,
    @SerialName("broadcast_platform")
    val broadcastPlatform: String = "",
    @SerialName("channel")
    val channel: Channel = Channel(),
    @SerialName("community_id")
    val communityId: String = "",
    @SerialName("community_ids")
    val communityIds: List<String> = listOf(),
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("delay")
    val delay: Int = 0,
    @SerialName("game")
    val game: String = "",
    @SerialName("_id")
    val id: Long = 0,
    @SerialName("is_playlist")
    val isPlaylist: Boolean = false,
    @SerialName("preview")
    val preview: Preview = Preview(),
    @SerialName("stream_type")
    val streamType: String = "",
    @SerialName("video_height")
    val videoHeight: Int = 0,
    @SerialName("viewers")
    val viewers: Int = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        TODO("channel"),
        parcel.readString()!!,
        TODO("communityIds"),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        TODO("preview"),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(averageFps)
        parcel.writeString(broadcastPlatform)
        parcel.writeString(communityId)
        parcel.writeString(createdAt)
        parcel.writeInt(delay)
        parcel.writeString(game)
        parcel.writeLong(id)
        parcel.writeByte(if (isPlaylist) 1 else 0)
        parcel.writeString(streamType)
        parcel.writeInt(videoHeight)
        parcel.writeInt(viewers)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<StreamInfo> {
        override fun createFromParcel(parcel: Parcel): StreamInfo = StreamInfo(parcel)

        override fun newArray(size: Int): Array<StreamInfo?> = arrayOfNulls(size)
    }
}

@Serializable
data class Channel(
    @SerialName("broadcaster_language")
    val broadcasterLanguage: String = "",
    @SerialName("broadcaster_software")
    val broadcasterSoftware: String = "",
    @SerialName("broadcaster_type")
    val broadcasterType: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("display_name")
    val displayName: String = "",
    @SerialName("followers")
    val followers: Int = 0,
    @SerialName("game")
    val game: String = "",
    @SerialName("_id")
    val id: Int = 0,
    @SerialName("language")
    val language: String = "",
    @SerialName("logo")
    val logo: String = "",
    @SerialName("mature")
    val mature: Boolean = false,
    @SerialName("name")
    val name: String = "",
    @SerialName("partner")
    val partner: Boolean = false,
    @SerialName("privacy_options_enabled")
    val privacyOptionsEnabled: Boolean = false,
    @SerialName("private_video")
    val privateVideo: Boolean = false,
    @SerialName("profile_banner")
    val profileBanner: String? = null,
    @SerialName("profile_banner_background_color")
    val profileBannerBackgroundColor: String = "",
    @SerialName("status")
    val status: String = "",
    @SerialName("updated_at")
    val updatedAt: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("video_banner")
    val videoBanner: String = "",
    @SerialName("views")
    val views: Int = 0,
)

@Serializable
data class Preview(
    @SerialName("large")
    val large: String = "",
    @SerialName("medium")
    val medium: String = "",
    @SerialName("small")
    val small: String = "",
    @SerialName("template")
    val template: String = "",
)

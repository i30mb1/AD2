package n7.ad2.data.source.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class StreamGQL(
    @Json(name = "stream")
    val stream: StreamInfo = StreamInfo(),
)

@JsonClass(generateAdapter = true)
data class StreamInfo(
    @Json(name = "average_fps")
    val averageFps: Int = 0,
    @Json(name = "broadcast_platform")
    val broadcastPlatform: String = "",
    @Json(name = "channel")
    val channel: Channel = Channel(),
    @Json(name = "community_id")
    val communityId: String = "",
    @Json(name = "community_ids")
    val communityIds: List<Any> = listOf(),
    @Json(name = "created_at")
    val createdAt: String = "",
    @Json(name = "delay")
    val delay: Int = 0,
    @Json(name = "game")
    val game: String = "",
    @Json(name = "_id")
    val id: Long = 0,
    @Json(name = "is_playlist")
    val isPlaylist: Boolean = false,
    @Json(name = "preview")
    val preview: Preview = Preview(),
    @Json(name = "stream_type")
    val streamType: String = "",
    @Json(name = "video_height")
    val videoHeight: Int = 0,
    @Json(name = "viewers")
    val viewers: Int = 0,
)

@JsonClass(generateAdapter = true)
data class Channel(
    @Json(name = "broadcaster_language")
    val broadcasterLanguage: String = "",
    @Json(name = "broadcaster_software")
    val broadcasterSoftware: String = "",
    @Json(name = "broadcaster_type")
    val broadcasterType: String = "",
    @Json(name = "created_at")
    val createdAt: String = "",
    @Json(name = "description")
    val description: String = "",
    @Json(name = "display_name")
    val displayName: String = "",
    @Json(name = "followers")
    val followers: Int = 0,
    @Json(name = "game")
    val game: String = "",
    @Json(name = "_id")
    val id: Int = 0,
    @Json(name = "language")
    val language: String = "",
    @Json(name = "logo")
    val logo: String = "",
    @Json(name = "mature")
    val mature: Boolean = false,
    @Json(name = "name")
    val name: String = "",
    @Json(name = "partner")
    val partner: Boolean = false,
    @Json(name = "privacy_options_enabled")
    val privacyOptionsEnabled: Boolean = false,
    @Json(name = "private_video")
    val privateVideo: Boolean = false,
    @Json(name = "profile_banner")
    val profileBanner: Any? = null,
    @Json(name = "profile_banner_background_color")
    val profileBannerBackgroundColor: String = "",
    @Json(name = "status")
    val status: String = "",
    @Json(name = "updated_at")
    val updatedAt: String = "",
    @Json(name = "url")
    val url: String = "",
    @Json(name = "video_banner")
    val videoBanner: String = "",
    @Json(name = "views")
    val views: Int = 0,
)

@JsonClass(generateAdapter = true)
data class Preview(
    @Json(name = "large")
    val large: String = "",
    @Json(name = "medium")
    val medium: String = "",
    @Json(name = "small")
    val small: String = "",
    @Json(name = "template")
    val template: String = "",
)
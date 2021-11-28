package n7.ad2.streams.internal.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Streams(
    @Json(name = "data")
    val list: List<Stream> = listOf(),
    @Json(name = "pagination")
    val pagination: Pagination = Pagination(),
)

@JsonClass(generateAdapter = true)
internal data class Stream(
    @Json(name = "game_id")
    val gameId: String = "",
    @Json(name = "game_name")
    val gameName: String = "",
    @Json(name = "id")
    val id: String = "",
    @Json(name = "is_mature")
    val isMature: Boolean = false,
    @Json(name = "language")
    val language: String = "",
    @Json(name = "started_at")
    val startedAt: String = "",
    @Json(name = "tag_ids")
    val tagIds: List<String> = listOf(),
    @Json(name = "thumbnail_url")
    val thumbnailUrl: String = "",
    @Json(name = "title")
    val title: String = "",
    @Json(name = "type")
    val type: String = "",
    @Json(name = "user_id")
    val userId: String = "",
    @Json(name = "user_login")
    val userLogin: String = "",
    @Json(name = "user_name")
    val userName: String = "",
    @Json(name = "viewer_count")
    val viewerCount: Int = 0,
)

@JsonClass(generateAdapter = true)
internal data class Pagination(
    @Json(name = "cursor")
    val cursor: String = "",
)

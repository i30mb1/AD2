package n7.ad2.drawer.internal.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal enum class VOMenuType { HEROES, ITEMS, NEWS, TOURNAMENTS, STREAMS, GAMES, UNKNOWN }

@JsonClass(generateAdapter = true)
internal data class Settings(
    @Json(name = "menu")
    val menu: List<Menu>? = emptyList(),
    @Json(name = "version")
    val version: Int? = 0,
)

@JsonClass(generateAdapter = true)
internal data class Menu(
    @Json(name = "type")
    val type: VOMenuType?,
    @Json(name = "isEnable")
    val isEnable: Boolean = true,
)
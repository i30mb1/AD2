package n7.ad2.data.source.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class VOMenuType { HEROES, ITEMS, NEWS, TOURNAMENTS, STREAMS, GAMES, UNKNOWN }

private val defaultMenu = listOf(
    Menu(VOMenuType.HEROES),
    Menu(VOMenuType.ITEMS),
    Menu(VOMenuType.NEWS),
    Menu(VOMenuType.TOURNAMENTS),
    Menu(VOMenuType.STREAMS),
    Menu(VOMenuType.GAMES),
)

@JsonClass(generateAdapter = true)
data class Settings(
    @Json(name = "menu")
    val menu: List<Menu> = defaultMenu,
    @Json(name = "version")
    val version: Int = 0,
)

@JsonClass(generateAdapter = true)
data class Menu(
    @Json(name = "type")
    val type: VOMenuType,
    @Json(name = "isEnable")
    val isEnable: Boolean = true,
)
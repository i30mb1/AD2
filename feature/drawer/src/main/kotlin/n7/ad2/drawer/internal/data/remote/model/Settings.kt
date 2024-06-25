package n7.ad2.drawer.internal.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import n7.ad2.drawer.internal.data.remote.adapter.StringVOMenuTypeAdapter

internal enum class VOMenuType { HEROES, ITEMS, NEWS, TOURNAMENTS, STREAMS, GAMES, UNKNOWN }

@Serializable
internal data class Settings(
    @SerialName("menu")
    val menu: List<Menu>? = emptyList(),
    @SerialName("version")
    val version: Int? = 0,
)

@Serializable(with = StringVOMenuTypeAdapter::class)
internal data class Menu(
    @SerialName("type")
    val type: VOMenuType,
    @SerialName("isEnable")
    val isEnable: Boolean = true,
)
package n7.ad2.drawer.internal.domain.vo

import n7.ad2.drawer.internal.data.remote.model.VOMenuType

internal data class VOMenu(
    val type: VOMenuType,
    val title: String,
    val isEnable: Boolean,
    val isSelected: Boolean = false,
)


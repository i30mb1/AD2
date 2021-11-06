package n7.ad2.ui.vo

import n7.ad2.data.source.remote.model.VOMenuType

data class VOMenu(
    val type: VOMenuType,
    val title: String,
    val isEnable: Boolean,
    val isSelected: Boolean = false,
)


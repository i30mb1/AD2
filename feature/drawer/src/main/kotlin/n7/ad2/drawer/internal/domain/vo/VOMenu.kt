package n7.ad2.drawer.internal.domain.vo

import androidx.annotation.StringRes
import n7.ad2.drawer.internal.data.remote.model.VOMenuType

internal data class VOMenu(val type: VOMenuType, val title: String, @StringRes val subtitle: Int, val isEnable: Boolean, val isSelected: Boolean = false, val info: VOMenuInfo? = null)

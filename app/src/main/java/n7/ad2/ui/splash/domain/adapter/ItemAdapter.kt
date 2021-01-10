package n7.ad2.ui.splash.domain.adapter

import n7.ad2.data.source.local.model.LocalItem
import n7.ad2.ui.splash.domain.model.AssetsItem

fun AssetsItem.toLocalItem(): LocalItem {
    return LocalItem(
        name = this.name,
        type = this.section,
        viewedByUser = false,
    )
}
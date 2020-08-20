package n7.ad2.ui.items.domain.adapter

import n7.ad2.data.source.local.model.LocalItem
import n7.ad2.ui.items.domain.vo.VOItem

fun LocalItem.toVO(): VOItem {
    return VOItem().also {
        it.name = name
        it.image = "file:///android_asset/$assetsPath/full.png"
    }
}
package n7.ad2.ui.items.domain.adapter

import n7.ad2.data.source.local.model.LocalItem
import n7.ad2.ui.items.domain.vo.VOItem
import n7.ad2.ui.items.domain.vo.VOItemBody

fun LocalItem.toVOItemBody(): VOItemBody = VOItemBody(
    name,
    "file:///android_asset/$assetsPath/full.png",
    viewedByUser,
)

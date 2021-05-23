package n7.ad2.ui.items.domain.adapter

import n7.ad2.data.source.local.ItemRepository
import n7.ad2.data.source.local.model.LocalItem
import n7.ad2.ui.items.domain.vo.VOItemBody

fun LocalItem.toVOItemBody() = VOItemBody(
    name,
    ItemRepository.getFullUrlItemImage(name),
    viewedByUser,
)

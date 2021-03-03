package n7.ad2.ui.itemInfo.domain.adapter

import n7.ad2.data.source.local.ItemRepository
import n7.ad2.ui.itemInfo.domain.vo.VORecipe

fun String.toVORecipe(): VORecipe = VORecipe(
    ItemRepository.getFullUrlItemImage(this),
    this,
)
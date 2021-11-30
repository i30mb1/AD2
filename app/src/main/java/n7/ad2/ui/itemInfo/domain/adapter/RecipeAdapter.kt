package n7.ad2.ui.itemInfo.domain.adapter

import n7.ad2.ui.itemInfo.domain.vo.VORecipe

fun String.toVORecipe(): VORecipe = VORecipe(
    n7.ad2.repositories.ItemRepository.getFullUrlItemImage(this),
    this,
)
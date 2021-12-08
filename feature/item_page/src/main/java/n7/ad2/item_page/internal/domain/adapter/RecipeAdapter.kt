package n7.ad2.item_page.internal.domain.adapter

import n7.ad2.item_page.internal.domain.vo.VORecipe
import n7.ad2.repositories.ItemRepository

fun String.toVORecipe(): VORecipe = VORecipe(
    ItemRepository.getFullUrlItemImage(this),
    this,
)
package n7.ad2.item_page.internal.domain.vo

import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderPlayableViewHolder
import n7.ad2.ui.adapter.ImageLineViewHolder

sealed class VOItemInfo {
    data class Title(val data: HeaderPlayableViewHolder.Data) : VOItemInfo()
    data class TextLine(val title: String) : VOItemInfo()
    data class ImageLine(val data: ImageLineViewHolder.Data) : VOItemInfo()
    data class Recipe(val itemName: String, val urlItemImage: String, val recipes: List<VORecipe>) : VOItemInfo()
    data class Body(val data: BodyViewHolder.Data) : VOItemInfo()
}
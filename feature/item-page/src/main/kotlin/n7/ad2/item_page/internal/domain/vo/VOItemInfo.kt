package n7.ad2.item_page.internal.domain.vo

import android.text.SpannableString
import androidx.annotation.DrawableRes
import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderComplexViewHolder

sealed class VOItemInfo {
    data class Title(val data: HeaderComplexViewHolder.Data) : VOItemInfo()
    data class TextLine(val title: String) : VOItemInfo()
    data class Recipe(val itemName: String, val urlItemImage: String, val recipes: List<VORecipe>) : VOItemInfo()
    data class Body(val data: BodyViewHolder.Data) : VOItemInfo()
}

data class VOItemInfoLineImage(val body: SpannableString, @DrawableRes val drawable: Int) : VOItemInfo()


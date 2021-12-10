package n7.ad2.item_page.internal.domain.vo

import android.text.SpannableString
import androidx.annotation.DrawableRes

sealed class VOItemInfo {
    data class Title(val title: String, val audioUrl: String) : VOItemInfo()
    data class TextLine(val title: String) : VOItemInfo()
    data class Recipe(val urlItemImage: String, val recipes: List<VORecipe>) : VOItemInfo()
}

data class VOItemInfoLineImage(val body: SpannableString, @DrawableRes val drawable: Int) : VOItemInfo()
data class VOItemInfoBody(val body: SpannableString) : VOItemInfo()

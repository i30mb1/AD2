package n7.ad2.ui.itemInfo.domain.vo

import android.text.SpannableString
import androidx.annotation.DrawableRes
import n7.ad2.hero_page.internal.pager.Playable

sealed class ItemInfo
data class VOItemInfoTitle(val title: String, override val audioUrl: String) : ItemInfo(), Playable {
    override val isPlaying = false
}

data class VOItemInfoLine(val title: String) : ItemInfo()
data class VOItemInfoLineImage(val body: SpannableString, @DrawableRes val drawable: Int) : ItemInfo()
data class VOItemInfoRecipe(val urlItemImage: String, val recipes: List<VORecipe>) : ItemInfo()
data class VOItemInfoBody(val body: SpannableString) : ItemInfo()

package n7.ad2.ui.heroInfo.domain.vo

import android.text.SpannableString
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableBoolean
import n7.ad2.ui.heroPage.Playable
import n7.ad2.ui.itemInfo.domain.vo.VORecipe

sealed class VODescription
data class VOTitleWithIcon(val title: String, val hotkey: String?, val legacyKey: String?, override val audioUrl: String?) : VODescription(), Playable { override val isPlaying: ObservableBoolean = ObservableBoolean(false) }
data class VOTitleSimple(val title: String) : VODescription()
data class VOBodyRecipe(val recipes: List<VORecipe>) : VODescription()
data class VOBodyTalent(val leftPart: String, val lvl: Int, val rightPart: String) : VODescription()
data class VOBodySimple(val body: String) : VODescription()
data class VOBodyWithSeparator(val body: SpannableString) : VODescription()
data class VOBodyWithImage(val body: SpannableString, @DrawableRes val drawable: Int) : VODescription()
data class VOBodyLine(val title: String) : VODescription()
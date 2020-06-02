package n7.ad2.ui.heroInfo.domain.vo

import android.text.SpannableString
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableBoolean

sealed class VODescription
data class VOTitleWithIcon(val title: String, val hotkey: String?, val legacyKey: String?, val audioUrl: String?) : VODescription() { val isPlaying: ObservableBoolean = ObservableBoolean(false) }
data class VOTitleSimple(val title: String) : VODescription()
data class VOBodySimple(val body: String) : VODescription()
data class VOBodyWithSeparator(val body: SpannableString) : VODescription()
data class VOBodyWithImage(val body: SpannableString, @DrawableRes val drawable: Int) : VODescription()
data class VOBodyLine(val title: String) : VODescription()
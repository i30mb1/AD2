package n7.ad2.ui.heroInfo.domain.vo

import android.text.SpannableString
import androidx.databinding.ObservableBoolean

class VODescription(
        val title: String,
        val hotkey: String? = null,
        val legacyKey: String? = null,
        val body: SpannableString,
        val effect1: String? = null,
        val effect2: String? = null,
        val effect3: String? = null,
        val mana: String? = null,
        val cooldown: SpannableString? = null,
        val audioUrl: String? = null
) {

    val isPlaying: ObservableBoolean = ObservableBoolean(false)
}
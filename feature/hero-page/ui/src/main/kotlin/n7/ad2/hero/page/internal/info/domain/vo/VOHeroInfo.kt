package n7.ad2.hero.page.internal.info.domain.vo

import android.text.SpannableString
import androidx.annotation.DrawableRes
import n7.ad2.hero.page.internal.info.HeroStatistics
import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderViewHolder

sealed class VOHeroInfo {
    data class HeaderSound(
        val title: String,
        val hotkey: String?,
        val legacyKey: String?,
        val isPlaying: Boolean,
        val soundUrl: String?,
    ) : VOHeroInfo()

    data class Header(val item: HeaderViewHolder.Data) : VOHeroInfo()
    data class Body(val item: BodyViewHolder.Data) : VOHeroInfo()
    data class Attributes(val urlHeroImage: String, val heroStatistics: HeroStatistics.Statistics, val isSelected: Boolean) : VOHeroInfo()
    data class Spells(val spells: List<VOSpell>) : VOHeroInfo()
}

sealed class VOSpell {
    data class Simple(val name: String, val urlSpellImage: String, val isSelected: Boolean) : VOSpell()
    data class Talent(val name: String, val isSelected: Boolean) : VOSpell()
}

data class VOBodyTalent(val talentLeft: String, val talentLvl: String, val talentRight: String) : VOHeroInfo()
data class VOBodySimple(val body: String) : VOHeroInfo()
data class VOBodyWithImage(val body: SpannableString, @DrawableRes val drawable: Int, val tip: String) : VOHeroInfo()
data class VOBodyLine(val title: SpannableString) : VOHeroInfo()
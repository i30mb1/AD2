package n7.ad2.ui.heroInfo.domain.vo

import android.text.SpannableString
import androidx.annotation.DrawableRes
import n7.ad2.ui.heroInfo.HeroStatistics
import n7.ad2.ui.heroPage.Playable

sealed class VOHeroInfo
data class VOHeroInfoHeaderSound(
    val title: String,
    val hotkey: String,
    val legacyKey: String,
    override val audioUrl: String,
    override val isPlaying: Boolean = false,
) : VOHeroInfo(), Playable

data class VOHeroSpells(val spells: List<VOSpell>) : VOHeroInfo()
data class VOHeroMainInformation(val urlHeroImage: String, val heroStatistics: HeroStatistics.Companion.Statistics, val voDescriptionList: List<VOHeroInfo>) : VOHeroInfo()
data class VOBodyTalent(val talentLeft: String, val talentLvl: String, val talentRight: String) : VOHeroInfo()
data class VOBodySimple(val body: String) : VOHeroInfo()
data class VOBodyWithSeparator(val body: SpannableString) : VOHeroInfo()
data class VOBodyWithImage(val body: SpannableString, @DrawableRes val drawable: Int, val tip: String) : VOHeroInfo()
data class VOBodyLine(val title: SpannableString) : VOHeroInfo()
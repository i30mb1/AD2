package n7.ad2.ui.heroInfo.domain.vo

import android.text.SpannableString
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import n7.ad2.ui.heroInfo.HeroStatistics
import n7.ad2.ui.heroPage.Playable
import n7.ad2.ui.itemInfo.domain.vo.VORecipe

sealed class VODescription
data class VOTitle(val title: String, val hotkey: String? = null, val legacyKey: String? = null, override val audioUrl: String? = null) : VODescription(), Playable { override val isPlaying = MutableLiveData(false) }
data class VOHeroSpells(val spells: List<VOSpell>) : VODescription()
data class VOHeroMainInformation(val urlHeroImage: String, val heroStatistics: HeroStatistics.Companion.Statistics, val voDescriptionList: List<VODescription>) : VODescription()
data class VOBodyTalent(val talentLeft: String, val talentLvl: String, val talentRight: String) : VODescription()
data class VOBodySimple(val body: String) : VODescription()
data class VOBodyWithSeparator(val body: SpannableString) : VODescription()
data class VOBodyWithImage(val body: SpannableString, @DrawableRes val drawable: Int, val tip: String) : VODescription()
data class VOBodyLine(val title: SpannableString) : VODescription()
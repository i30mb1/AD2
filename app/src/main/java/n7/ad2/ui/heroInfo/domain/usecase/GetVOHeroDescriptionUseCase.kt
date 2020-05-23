package n7.ad2.ui.heroInfo.domain.usecase

import android.app.Application
import android.content.res.Resources
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.R
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.InfoPopupWindow
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val application: Application
) {

    companion object {
        const val SEPARATOR = "- "
        const val TAG_TALENT = "TagTalent"
    }

    private fun List<String>.toListWithDash(): String {
        val builder = StringBuilder()
        forEachIndexed { index, text ->
            builder.append(SEPARATOR)
            builder.append(text)
            if (index != lastIndex) builder.append(System.lineSeparator())
        }
        return builder.toString()
    }

    suspend operator fun invoke(localHeroDescription: LocalHeroDescription, localHero: LocalHero, theme: Resources.Theme): VOHeroDescription = withContext(ioDispatcher) {
        val voHeroDescription = VOHeroDescription()

        voHeroDescription.heroImagePath = "file:///android_asset/${localHero.assetsPath}/full.png"

        val voSpellList: List<VOSpell> = localHeroDescription.abilities.map {
            val descriptions = mutableListOf<VODescription>().apply {
                val cooldown = if(it.cooldown!=null) getSpannableTagTalent(it.cooldown, theme) else SpannableString("")
                val params = getSpannableTagTalent(it.params.toListWithDash(), theme)

                add(VODescription(it.spellName, it.hotKey, it.legacyKey, it.description, it.effects.getOrNull(0), it.effects.getOrNull(1), it.effects.getOrNull(2), it.mana, cooldown, it.spellAudio))
                add(VODescription(title = application.getString(R.string.hero_fragment_params), body = it.params.toListWithDash()))
                if (it.story != null) add(VODescription(title = application.getString(R.string.hero_fragment_story), body = it.story))
                add(VODescription(title = application.getString(R.string.hero_fragment_notes), body = it.notes.toListWithDash()))

            }

            VOSpell().apply {
                name = it.spellName
                selected = false
                image = "file:///android_asset/spells/${it.spellName}.png"
                listVODescriptions = descriptions
                spellAudio = it.spellAudio
            }
        }
        voHeroDescription.spells = voSpellList



        voHeroDescription
    }

    private fun getSpannableTagTalent(body: String, theme: Resources.Theme): SpannableString {
        val indexOf = body.indexOf(TAG_TALENT)
        val spannableString = SpannableString(body)
        if (indexOf == -1) return spannableString
        val icon = application.resources.getDrawable(R.drawable.talent, theme).apply {
            setBounds(0, 0, application.resources.getDimensionPixelSize(R.dimen.icon_in_description), application.resources.getDimensionPixelSize(R.dimen.icon_in_description))
        }
        spannableString.setSpan(ImageSpan(icon, DynamicDrawableSpan.ALIGN_BOTTOM), indexOf, indexOf + TAG_TALENT.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                InfoPopupWindow(widget, application.getString(R.string.info_talent))
            }

        }, indexOf, indexOf + TAG_TALENT.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString

    }

}
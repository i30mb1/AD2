package n7.ad2.ui.heroInfo.domain.usecase

import android.app.Application
import android.content.res.Resources
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import android.webkit.WebView.HitTestResult.IMAGE_TYPE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.BuildConfig
import n7.ad2.R
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.InfoPopupWindow
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOBodyLine
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VOBodyTalent
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithImage
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithSeparator
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import n7.ad2.ui.heroInfo.domain.vo.VOTitleSimple
import n7.ad2.ui.heroInfo.domain.vo.VOTitleWithIcon
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val application: Application
) {

    companion object {
        const val HERO_FULL_PHOTO_NAME = "full"
        const val HERO_FULL_PHOTO_TYPE = "png"
        const val HEROES_SPELL_FOLDER = "heroesSpell"
        const val SEPARATOR = "- "
        const val TAG_TALENT = "TagTalent"
        const val COLON = ": "
        const val SEPARATOR_TALENT = "^"
    }

    private fun List<String>.toStringListWithDashAfterColon(): String {
        val builder = SpannableStringBuilder()
        forEachIndexed { index, text ->
            builder.append(text)
            if (index != lastIndex) builder.append(System.lineSeparator())
        }
        var startIndex = 0
        var indexOf = builder.indexOf(COLON, startIndex)
        if (indexOf == -1) return builder.toString()

        do {
            builder.insert(indexOf + 1, "\n")
            startIndex++
            indexOf = builder.indexOf(COLON, startIndex)
        } while (indexOf != -1)

        return builder.toString()
    }

    private fun List<String>.toStringListWithDash(): String {
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
        voHeroDescription.heroImagePath = "file:///android_asset/${localHero.assetsPath}/$HERO_FULL_PHOTO_NAME.$HERO_FULL_PHOTO_TYPE"

        val spells: MutableList<VOSpell> = localHeroDescription.abilities.map {
            val descriptions = mutableListOf<VODescription>().apply {

                add(VOTitleWithIcon(it.spellName, it.hotKey, it.legacyKey, it.audioUrl))
                it.effects.forEach { add(VOBodyLine(it)) }
                add(VOBodySimple(it.description))
                it.cooldown?.let { add(VOBodyWithImage(spanWithDotaImages(it, theme), R.drawable.cooldown)) }
                it.mana?.let { add(VOBodyWithImage(spanWithDotaImages(it, theme), R.drawable.mana)) }

                add(VOTitleSimple(application.getString(R.string.hero_fragment_params)))
                add(VOBodyWithSeparator(spanWithDotaImages(it.params.toStringListWithDash(), theme)))

                it.story?.let {
                    add(VOTitleSimple(application.getString(R.string.hero_fragment_story)))
                    add(VOBodyWithSeparator(SpannableString(it)))
                }

                add(VOTitleSimple(application.getString(R.string.hero_fragment_notes)))
                add(VOBodyWithSeparator(SpannableString(it.notes.toStringListWithDash())))
            }

            VOSpell().apply {
                name = it.spellName
                image = "file:///android_asset/$HEROES_SPELL_FOLDER/${it.spellName}.$IMAGE_TYPE"
                listVODescriptions = descriptions
            }
        }.toMutableList()

        val talentListVoDescription = mutableListOf<VODescription>().apply {
            add(VOTitleSimple(application.getString(R.string.item_hero_personal_description_talents)))
            var talentLVL = 5
            localHeroDescription.talents.forEach {
                val parts = it.split(SEPARATOR_TALENT)
                add(VOBodyTalent(parts[0], talentLVL, parts[1]))
                talentLVL += 5
            }

            add(VOTitleSimple(application.getString(R.string.hero_fragment_tips)))
            add(VOBodyWithSeparator(SpannableString(localHeroDescription.talentTips.toStringListWithDash())))
        }
        spells.add(0, VOSpell().apply {
            name = application.getString(R.string.item_hero_personal_description_talents)
            image = Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.drawable.talent).toString()
            listVODescriptions = talentListVoDescription
        }
        )
        voHeroDescription.spells = spells

        val heroBio = mutableListOf<VODescription>().apply {
            add(VOTitleSimple(application.getString(R.string.hero_fragment_description)))
            add(VOBodyWithSeparator(SpannableString(localHeroDescription.description)))

            add(VOTitleSimple(application.getString(R.string.hero_fragment_bio)))
            add(VOBodyWithSeparator(SpannableString(localHeroDescription.history)))

            add(VOTitleSimple(application.getString(R.string.hero_fragment_trivia)))
            add(VOBodyWithSeparator(SpannableString(localHeroDescription.trivia.toStringListWithDash())))
        }
        voHeroDescription.heroBio = heroBio
        voHeroDescription.selectedDescriptionList = heroBio

        voHeroDescription
    }

    private fun spanWithDotaImages(body: String, theme: Resources.Theme): SpannableString {
        var startIndex = 0
        var indexOf = body.indexOf(TAG_TALENT, startIndex)
        val spannableString = SpannableString(body)
        if (indexOf == -1) return spannableString
        val icon = application.resources.getDrawable(R.drawable.talent, theme).apply {
            val imageSize = application.resources.getDimensionPixelSize(R.dimen.icon_in_description)
            setBounds(0, 0, imageSize, imageSize)
        }
        do {
            spannableString.setSpan(ImageSpan(icon, DynamicDrawableSpan.ALIGN_BOTTOM), indexOf, indexOf + TAG_TALENT.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    InfoPopupWindow(widget, application.getString(R.string.info_talent))
                }

            }, indexOf, indexOf + TAG_TALENT.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            startIndex++
            indexOf = body.indexOf(TAG_TALENT, startIndex)
        } while (indexOf != -1)

        return spannableString
    }

}
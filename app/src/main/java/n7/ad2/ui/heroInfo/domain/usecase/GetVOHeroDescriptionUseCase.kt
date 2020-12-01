package n7.ad2.ui.heroInfo.domain.usecase

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.BuildConfig
import n7.ad2.R
import n7.ad2.base.VOPopUpListener
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOBodyLine
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VOBodyTalent
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithImage
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithSeparator
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import n7.ad2.ui.heroInfo.domain.vo.VOTitle
import n7.ad2.utils.extension.toStringListWithDash
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val application: Application
) {

    companion object {
        const val HERO_FULL_PHOTO_NAME = "full"
        const val HERO_FULL_PHOTO_TYPE = "png"
        const val HEROES_SPELL_FOLDER = "heroesSpell"
        const val TAG_TALENT = "TagTalent"
        const val SEPARATOR_TALENT = "^"
    }

    suspend operator fun invoke(localHeroDescription: LocalHeroDescription, localHero: LocalHero): VOHeroDescription = withContext(ioDispatcher) {
        val voHeroDescription = VOHeroDescription()
        voHeroDescription.heroImagePath = "file:///android_asset/${localHero.assetsPath}/$HERO_FULL_PHOTO_NAME.$HERO_FULL_PHOTO_TYPE"

        val spells: MutableList<VOSpell> = localHeroDescription.abilities.map {
            val descriptions = mutableListOf<VODescription>().apply {

                add(VOTitle(it.spellName, it.hotKey, it.legacyKey, it.audioUrl))
                it.effects.forEach { add(VOBodyLine(it)) }
                add(VOBodySimple(it.description))
                it.cooldown?.let { add(VOBodyWithImage(spanWithDotaImages(it), R.drawable.cooldown)) }
                it.mana?.let { add(VOBodyWithImage(spanWithDotaImages(it), R.drawable.mana)) }

                add(VOTitle(application.getString(R.string.hero_fragment_params)))
                add(VOBodyWithSeparator(spanWithDotaImages(it.params.toStringListWithDash())))

                it.story?.let {
                    add(VOTitle(application.getString(R.string.hero_fragment_story)))
                    add(VOBodyWithSeparator(SpannableString(it)))
                }

                add(VOTitle(application.getString(R.string.hero_fragment_notes)))
                add(VOBodyWithSeparator(SpannableString(it.notes.toStringListWithDash())))
            }

            VOSpell().apply {
                name = it.spellName
                image = "file:///android_asset/$HEROES_SPELL_FOLDER/${it.spellName}.$HERO_FULL_PHOTO_TYPE"
                listVODescriptions = descriptions
            }
        }.toMutableList()

        val talentListVoDescription = mutableListOf<VODescription>().apply {
            add(VOTitle(application.getString(R.string.item_hero_personal_description_talents)))
            var talentLVL = 5
            localHeroDescription.talents.forEach {
                val parts = it.split(SEPARATOR_TALENT)
                add(VOBodyTalent(parts[0], talentLVL, parts[1]))
                talentLVL += 5
            }

            add(VOTitle(application.getString(R.string.tips)))
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
            add(VOTitle(application.getString(R.string.hero_fragment_description)))
            add(VOBodyWithSeparator(SpannableString(localHeroDescription.description)))

            add(VOTitle(application.getString(R.string.hero_fragment_bio)))
            add(VOBodyWithSeparator(SpannableString(localHeroDescription.history)))

            add(VOTitle(application.getString(R.string.hero_fragment_trivia)))
            add(VOBodyWithSeparator(SpannableString(localHeroDescription.trivia.toStringListWithDash())))
        }
        voHeroDescription.heroBio = heroBio
        voHeroDescription.selectedDescriptionList = heroBio

        voHeroDescription
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun spanWithDotaImages(body: String): SpannableString {
        var startIndex = 0
        var indexOf = body.indexOf(TAG_TALENT, startIndex)
        val spannableString = SpannableString(body)
        if (indexOf == -1) return spannableString
        val icon = application.resources.getDrawable(R.drawable.talent, null).apply {
            val imageSize = application.resources.getDimensionPixelSize(R.dimen.icon_in_description)
            setBounds(0, 0, imageSize, imageSize)
        }
        do {
            spannableString.setSpan(ImageSpan(icon, DynamicDrawableSpan.ALIGN_BOTTOM), indexOf, indexOf + TAG_TALENT.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(object : MyClickableSpan(application.getString(R.string.info_talent)) {

            }, indexOf, indexOf + TAG_TALENT.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            startIndex++
            indexOf = body.indexOf(TAG_TALENT, startIndex)
        } while (indexOf != -1)

        return spannableString
    }

}

open class MyClickableSpan(private val text: String) : ClickableSpan() {

    var listener: VOPopUpListener<String>? = null

    override fun onClick(widget: View) {
        listener?.onClickListener(widget, text)
    }

}
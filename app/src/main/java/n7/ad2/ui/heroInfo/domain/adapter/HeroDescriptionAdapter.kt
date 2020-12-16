package n7.ad2.ui.heroInfo.domain.adapter

import android.annotation.SuppressLint
import android.app.Application
import android.text.SpannableString
import n7.ad2.CustomHeroAttrs
import n7.ad2.R
import n7.ad2.data.source.local.Repository
import n7.ad2.ui.heroInfo.domain.model.Ability
import n7.ad2.ui.heroInfo.domain.model.MainAttribute
import n7.ad2.ui.heroInfo.domain.vo.VOBodyLine
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithImage
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithSeparator
import n7.ad2.ui.heroInfo.domain.vo.VOHeroAttrs
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import n7.ad2.ui.heroInfo.domain.vo.VOTitle
import n7.ad2.utils.extension.spanWithDotaImages
import n7.ad2.utils.extension.toStringListWithDash

fun MainAttribute.toVOHeroAttrs(heroName: String): VOHeroAttrs = VOHeroAttrs(
    Repository.getFullUrlHeroImage(heroName),
    CustomHeroAttrs.Companion.HeroAttrs(this.attrStrength, this.attrAgility, this.attrIntelligence)
)

@ExperimentalStdlibApi
fun Ability.toVOSpell(application: Application) = VOSpell(
    this.spellName,
    Repository.getFullUrlHeroSpell(this.spellName),
    buildList {
        add(VOTitle(spellName, hotKey, legacyKey, audioUrl))
        effects.forEach { title -> add(VOBodyLine(title)) }
        add(VOBodySimple(description))
        cooldown?.let { cooldown -> add(VOBodyWithImage(cooldown.spanWithDotaImages(application), R.drawable.cooldown)) }
        mana?.let { mana -> add(VOBodyWithImage(mana.spanWithDotaImages(application), R.drawable.mana)) }
        params?.let { params ->
            add(VOTitle(application.getString(R.string.hero_fragment_params)))
            add(VOBodyWithSeparator((params.toStringListWithDash()).spanWithDotaImages(application)))
        }
        story?.let { story ->
            add(VOTitle(application.getString(R.string.hero_fragment_story)))
            add(VOBodyWithSeparator(SpannableString(story)))
        }
        notes?.let { notes ->
            add(VOTitle(application.getString(R.string.hero_fragment_notes)))
            add(VOBodyWithSeparator(SpannableString(notes.toStringListWithDash())))
        }
    }
)


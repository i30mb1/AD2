package n7.ad2.ui.heroInfo.domain.adapter

import android.app.Application
import android.text.SpannableString
import n7.ad2.ui.heroInfo.HeroStatistics
import n7.ad2.R
import n7.ad2.data.source.local.Repository
import n7.ad2.ui.heroInfo.domain.model.Ability
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOBodyLine
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VOBodyTalent
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithImage
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithSeparator
import n7.ad2.ui.heroInfo.domain.vo.VOHeroMainInformation
import n7.ad2.ui.heroInfo.domain.vo.VOHeroSpells
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import n7.ad2.ui.heroInfo.domain.vo.VOTitle
import n7.ad2.utils.extension.spanWithDotaImages
import n7.ad2.utils.extension.toStringListWithDash

@ExperimentalStdlibApi
fun LocalHeroDescription.toVOHeroAttrs(application: Application, heroName: String): VOHeroMainInformation = VOHeroMainInformation(
    Repository.getFullUrlHeroImage(heroName),
    HeroStatistics.Companion.Statistics(mainAttributes.attrStrength, mainAttributes.attrAgility, mainAttributes.attrIntelligence),
    buildList {
        add(VOTitle(application.getString(R.string.hero_fragment_description)))
        add(VOBodyWithSeparator(SpannableString(description)))
        add(VOTitle(application.getString(R.string.hero_fragment_bio)))
        add(VOBodyWithSeparator(SpannableString(history)))
        trivia?.let { trivia ->
            add(VOTitle(application.getString(R.string.hero_fragment_trivia)))
            add(VOBodyWithSeparator(SpannableString(trivia.toStringListWithDash())))
        }
    }
)

@ExperimentalStdlibApi
fun LocalHeroDescription.toVOHeroSpells(application: Application) = VOHeroSpells(abilities.map { it.toVOSpell(application) })

@ExperimentalStdlibApi
fun Ability.toVOSpell(application: Application) = VOSpell(
    this.spellName,
    Repository.getFullUrlHeroSpell(this.spellName),
    buildList {
        add(VOTitle(spellName, hotKey, legacyKey, audioUrl))
        effects.forEach { title -> add(VOBodyLine(title)) }
        talents?.forEach { talent -> add(VOBodyTalent(talent.talentLeft, talent.talentLvl, talent.talentRight)) }
        description?.let { description -> add(VOBodySimple(description)) }
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


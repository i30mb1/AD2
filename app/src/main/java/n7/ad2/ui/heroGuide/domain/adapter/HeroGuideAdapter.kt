package n7.ad2.ui.heroGuide.domain.adapter

import n7.ad2.data.source.local.Repository
import n7.ad2.ui.heroGuide.VOHeroFlowItem
import n7.ad2.ui.heroGuide.VOHeroFlowSpell
import n7.ad2.ui.heroGuide.domain.model.HeroWithWinrate
import n7.ad2.ui.heroGuide.domain.model.Spell
import n7.ad2.ui.heroGuide.domain.vo.VOEasyToWinHeroes
import n7.ad2.ui.heroGuide.domain.vo.VOGuideSpellBuild
import n7.ad2.ui.heroGuide.domain.vo.VOHardToWinHeroes

fun List<HeroWithWinrate>.toVOHardToWinHeroes(): VOHardToWinHeroes = VOHardToWinHeroes(map { it.toVOHeroFlowItem() })

fun List<HeroWithWinrate>.toVOEasyToWinHeroes(): VOEasyToWinHeroes = VOEasyToWinHeroes(map { it.toVOHeroFlowItem() })

private fun HeroWithWinrate.toVOHeroFlowItem(): VOHeroFlowItem = VOHeroFlowItem(
    heroName,
    Repository.getFullUrlHeroImage(heroName),
    "${heroWinrate}%"
)

fun List<Spell>.toVOGuideSpellBuild(): VOGuideSpellBuild = VOGuideSpellBuild(
    map { VOHeroFlowSpell(it.spellName, Repository.getFullUrlHeroSpell(it.spellName), it.spellOrder) }
)
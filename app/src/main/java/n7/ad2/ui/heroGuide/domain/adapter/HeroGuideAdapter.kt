package n7.ad2.ui.heroGuide.domain.adapter

import n7.ad2.data.source.local.ItemRepository
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.ui.heroGuide.VOHeroFlowHeroItem
import n7.ad2.ui.heroGuide.VOHeroFlowItem
import n7.ad2.ui.heroGuide.VOHeroFlowSpell
import n7.ad2.ui.heroGuide.VOHeroFlowStartingHeroItem
import n7.ad2.ui.heroGuide.domain.model.HeroItem
import n7.ad2.ui.heroGuide.domain.model.HeroWithWinrate
import n7.ad2.ui.heroGuide.domain.model.Spell
import n7.ad2.ui.heroGuide.domain.vo.VOGuideEasyToWinHeroes
import n7.ad2.ui.heroGuide.domain.vo.VOGuideHardToWinHeroes
import n7.ad2.ui.heroGuide.domain.vo.VOGuideHeroItems
import n7.ad2.ui.heroGuide.domain.vo.VOGuideSpellBuild
import n7.ad2.ui.heroGuide.domain.vo.VOGuideStartingHeroItems

fun List<HeroWithWinrate>.toVOHardToWinHeroes(): VOGuideHardToWinHeroes = VOGuideHardToWinHeroes(map { it.toVOHeroFlowItem() })

fun List<HeroWithWinrate>.toVOEasyToWinHeroes(): VOGuideEasyToWinHeroes = VOGuideEasyToWinHeroes(map { it.toVOHeroFlowItem() })

private fun HeroWithWinrate.toVOHeroFlowItem(): VOHeroFlowItem = VOHeroFlowItem(
    heroName,
    HeroRepository.getFullUrlHeroImage(heroName),
    "${heroWinrate}%"
)

fun List<Spell>.toVOGuideSpellBuild(): VOGuideSpellBuild = VOGuideSpellBuild(
    map { VOHeroFlowSpell(it.spellName, HeroRepository.getFullUrlHeroSpell(it.spellName), it.spellOrder) }
)

fun List<HeroItem>.toVOGuideStartingHeroItems(): VOGuideStartingHeroItems = VOGuideStartingHeroItems(
    map { VOHeroFlowStartingHeroItem(it.itemName, ItemRepository.getFullUrlItemImage(it.itemName)) }
)

fun List<HeroItem>.toVOGuideHeroItems(): VOGuideHeroItems {
    var lastItemTime = ""
    val result = map {
        val itemTime = if (lastItemTime == it.itemTime) null else it.itemTime
        lastItemTime = it.itemTime
        VOHeroFlowHeroItem(it.itemName, ItemRepository.getFullUrlItemImage(it.itemName), itemTime)
    }
    return VOGuideHeroItems(result)
}
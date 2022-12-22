package n7.ad2.heropage.internal.guides.domain.adapter

import n7.ad2.heropage.internal.guides.VOHeroFlowHeroItem
import n7.ad2.heropage.internal.guides.VOHeroFlowItem
import n7.ad2.heropage.internal.guides.VOHeroFlowSpell
import n7.ad2.heropage.internal.guides.VOHeroFlowStartingHeroItem
import n7.ad2.heropage.internal.guides.domain.model.HeroItem
import n7.ad2.heropage.internal.guides.domain.model.HeroWithWinrate
import n7.ad2.heropage.internal.guides.domain.model.Spell
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideEasyToWinHeroes
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideHardToWinHeroes
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideHeroItems
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideSpellBuild
import n7.ad2.heropage.internal.guides.domain.vo.VOGuideStartingHeroItems
import n7.ad2.repositories.HeroRepository
import n7.ad2.repositories.ItemRepository

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
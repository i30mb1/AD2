package n7.ad2.hero.page.internal.guides.domain.adapter

import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHero
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuidePhase
import n7.ad2.heroes.domain.model.HeroItem
import n7.ad2.heroes.domain.model.HeroWithWinrate

private const val ITEM_CDN = "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/items"

private fun itemImageUrl(itemName: String) = "$ITEM_CDN/$itemName.png"

fun List<HeroWithWinrate>.toVOGuideHeroes(): List<VOGuideHero> = map { VOGuideHero(it.heroName, it.avatarUrl, it.heroWinrate, "${it.heroWinrate}%") }

fun List<HeroItem>.toVOGuideItems(): List<VOGuideItem> = map { VOGuideItem(it.itemName, itemImageUrl(it.itemName)) }

/** OpenDota marks every build item with its game stage, the screen shows one row per stage. */
fun List<HeroItem>.toVOGuidePhases(): List<VOGuidePhase> = groupBy { it.itemTime }
    .mapNotNull { (time, items) ->
        val phase = when (time) {
            "Early" -> VOGuidePhase.Phase.EARLY
            "Mid" -> VOGuidePhase.Phase.MID
            "Late" -> VOGuidePhase.Phase.LATE
            else -> return@mapNotNull null
        }
        VOGuidePhase(phase, items.toVOGuideItems())
    }
    .sortedBy { it.phase }

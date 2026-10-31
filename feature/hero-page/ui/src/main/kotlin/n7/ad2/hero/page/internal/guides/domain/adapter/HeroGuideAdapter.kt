package n7.ad2.hero.page.internal.guides.domain.adapter

import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideBar
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHero
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuidePhase
import n7.ad2.hero.page.internal.guides.domain.vo.VOSkillBuild
import n7.ad2.hero.page.internal.guides.domain.vo.VOSkillRow
import n7.ad2.heroes.domain.model.HeroItem
import n7.ad2.heroes.domain.model.DurationWinrate
import n7.ad2.heroes.domain.model.HeroWithWinrate
import n7.ad2.heroes.domain.model.RankWinrate
import n7.ad2.heroes.domain.model.SkillUpgrade

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

fun List<RankWinrate>.toVORankBars(): List<VOGuideBar> = map { VOGuideBar(VOGuideBar.Label.Rank(it.rank), it.winrate) }

fun List<DurationWinrate>.toVODurationBars(): List<VOGuideBar> = map { VOGuideBar(VOGuideBar.Label.Minutes(it.fromMinute, it.toMinute), it.winrate) }

fun List<SkillUpgrade>.toVOSkillBuild(): VOSkillBuild? {
    if (isEmpty()) return null
    val abilities = filter { it.kind == SkillUpgrade.Kind.ABILITY }.map { it.abilityKey }.distinct().map { key ->
        VOSkillRow(first { it.abilityKey == key }.imageUrl, VOSkillRow.Kind.ABILITY, levelsOf { it.abilityKey == key })
    }
    val extra = listOf(SkillUpgrade.Kind.TALENT to VOSkillRow.Kind.TALENT, SkillUpgrade.Kind.ATTRIBUTES to VOSkillRow.Kind.ATTRIBUTES)
        .mapNotNull { (kind, rowKind) -> levelsOf { it.kind == kind }.takeIf { it.isNotEmpty() }?.let { VOSkillRow("", rowKind, it) } }
    return VOSkillBuild(levels = size, rows = abilities + extra)
}

private inline fun List<SkillUpgrade>.levelsOf(predicate: (SkillUpgrade) -> Boolean): Set<Int> =
    mapIndexedNotNull { index, upgrade -> if (predicate(upgrade)) index + 1 else null }.toSet()

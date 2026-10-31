package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHero
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuidePhase
import n7.ad2.heroes.domain.model.Playstyle

/** The "In short" card on top of the "Info" page: what to know about the hero before the game starts. */
data class VOHeroSummary(
    val pros: List<String>,
    val cons: List<String>,
    val threats: List<VOGuideHero>,
    val keyItems: List<VOGuideItem>,
)

private const val POINTS = 2
private const val THREATS = 4
private const val KEY_ITEMS = 3

/**
 * Pros and cons come from the wiki of the app language, threats and items from the OpenDota guide, which needs
 * the internet. Each part is left out when its source has nothing; null when there is nothing at all.
 */
fun heroSummary(playstyle: Playstyle?, guide: VOGuide?): VOHeroSummary? {
    val summary = VOHeroSummary(
        pros = playstyle?.pros.orEmpty().take(POINTS),
        cons = playstyle?.cons.orEmpty().take(POINTS),
        threats = guide?.hardToWin.orEmpty().take(THREATS),
        // mid game items are what the hero is built around, early ones are mostly boots and stats
        keyItems = guide?.phases?.firstOrNull { it.phase == VOGuidePhase.Phase.MID }?.items.orEmpty().take(KEY_ITEMS),
    )
    val isEmpty = summary.pros.isEmpty() && summary.cons.isEmpty() && summary.threats.isEmpty() && summary.keyItems.isEmpty()
    return if (isEmpty) null else summary
}

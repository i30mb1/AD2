package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.Facet
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.HeroWiki
import n7.ad2.heroes.domain.model.Playstyle
import n7.ad2.heroes.domain.model.TalentLevel
import n7.ad2.heroes.domain.model.WikiSection
import org.junit.Assert.assertEquals
import org.junit.Test

class InfoSectionTest {

    private val section = WikiSection("title", 2, emptyList())

    private fun info(description: HeroDescription, abilities: List<Ability> = emptyList()) = VOHeroInfo(
        heroImageUrl = "",
        description = description,
        abilityGroups = if (abilities.isEmpty()) emptyList() else listOf(VOAbilityGroup(VOAbilityGroup.Group.MAIN, abilities)),
    )

    @Test
    fun `a hero with everything gets every section in the order of the chips`() {
        val description = HeroDescription(
            talents = listOf(TalentLevel(10, "a", "b")),
            facets = listOf(Facet("facet", "")),
            bio = "bio",
            wiki = HeroWiki(recommendedItems = listOf(section), counters = listOf(section), playstyle = Playstyle("text", emptyList(), emptyList())),
        )
        assertEquals(
            listOf(
                InfoSection.ABILITIES,
                InfoSection.STATS,
                InfoSection.TALENTS,
                InfoSection.FACETS,
                InfoSection.STRATEGY,
                InfoSection.COUNTERS,
                InfoSection.ITEMS,
                InfoSection.LORE,
            ),
            info(description, abilities = listOf(Ability())).sections(),
        )
    }

    @Test
    fun `sections without data have no chip, stats are always there`() {
        assertEquals(listOf(InfoSection.STATS), info(HeroDescription()).sections())
    }

    @Test
    fun `tips alone are enough for strategy and trivia alone for lore`() {
        val description = HeroDescription(trivia = listOf("fact"), wiki = HeroWiki(tips = listOf(section)))
        assertEquals(listOf(InfoSection.STATS, InfoSection.STRATEGY, InfoSection.LORE), info(description).sections())
    }
}

package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.heroes.domain.model.Facet
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.HeroWiki
import n7.ad2.heroes.domain.model.PatchChange
import n7.ad2.heroes.domain.model.Playstyle
import n7.ad2.heroes.domain.model.WikiSection
import org.junit.Assert.assertEquals
import org.junit.Test

class InfoRowTest {

    private val section = WikiSection("title", 2, emptyList())

    private fun info(description: HeroDescription) = VOHeroInfo(heroImageUrl = "", description = description, abilityGroups = emptyList())

    private val everything = HeroDescription(
        facets = listOf(Facet("facet", "")),
        bio = "bio",
        changes = listOf(PatchChange("7.41d", 0, emptyList(), emptyList(), emptyList())),
        wiki = HeroWiki(recommendedItems = listOf(section), counters = listOf(section), playstyle = Playstyle("text", emptyList(), emptyList())),
    )

    @Test
    fun `a hero with everything gets every row, rarely needed ones last`() {
        assertEquals(
            listOf(InfoRow.FACETS, InfoRow.TIPS, InfoRow.ITEMS, InfoRow.COUNTERS, InfoRow.CHANGES, InfoRow.LORE),
            info(everything).rows(),
        )
    }

    @Test
    fun `with OpenDota matchups the counters open from their own section, not from a row`() {
        assertEquals(
            listOf(InfoRow.FACETS, InfoRow.TIPS, InfoRow.ITEMS, InfoRow.CHANGES, InfoRow.LORE),
            info(everything).rows(hasMatchups = true),
        )
    }

    @Test
    fun `rows without data are not shown, stats are on the page itself`() {
        assertEquals(emptyList<InfoRow>(), info(HeroDescription()).rows())
    }

    @Test
    fun `tips alone are enough for the tips row and trivia alone for lore`() {
        val description = HeroDescription(trivia = listOf("fact"), wiki = HeroWiki(tips = listOf(section)))
        assertEquals(listOf(InfoRow.TIPS, InfoRow.LORE), info(description).rows())
    }
}

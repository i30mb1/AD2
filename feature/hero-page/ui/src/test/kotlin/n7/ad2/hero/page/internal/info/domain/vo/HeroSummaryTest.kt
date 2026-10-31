package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHero
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuidePhase
import n7.ad2.heroes.domain.model.Playstyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HeroSummaryTest {

    private fun hero(name: String) = VOGuideHero(name, "url/$name", 40.0, "40%")
    private fun item(name: String) = VOGuideItem(name, "url/$name")

    private val guide = VOGuide(
        winrate = "",
        matches = "",
        proPicks = 0,
        proWinrate = null,
        proBans = 0,
        rankWinrates = emptyList(),
        durationWinrates = emptyList(),
        skillBuild = null,
        startingItems = listOf(item("tango")),
        phases = listOf(
            VOGuidePhase(VOGuidePhase.Phase.EARLY, listOf(item("treads"))),
            VOGuidePhase(VOGuidePhase.Phase.MID, listOf(item("bfury"), item("yasha"), item("manta"), item("diadem"))),
        ),
        easyToWin = emptyList(),
        hardToWin = listOf("a", "b", "c", "d", "e").map(::hero),
    )

    private val playstyle = Playstyle("text", pros = listOf("p1", "p2", "p3"), cons = listOf("c1", "c2", "c3"))

    @Test
    fun `two pros and two cons, the hardest matchups and the core items of mid game`() {
        val summary = heroSummary(playstyle, guide)!!
        assertEquals(listOf("p1", "p2"), summary.pros)
        assertEquals(listOf("c1", "c2"), summary.cons)
        assertEquals(listOf("a", "b", "c", "d"), summary.threats.map { it.heroName })
        assertEquals(listOf("bfury", "yasha", "manta"), summary.keyItems.map { it.itemName })
    }

    @Test
    fun `without a guide the card keeps the wiki part`() {
        val summary = heroSummary(playstyle, guide = null)!!
        assertEquals(emptyList<VOGuideHero>(), summary.threats)
        assertEquals(emptyList<VOGuideItem>(), summary.keyItems)
    }

    @Test
    fun `no card when there is nothing to put into it`() {
        assertNull(heroSummary(playstyle = null, guide = null))
    }
}

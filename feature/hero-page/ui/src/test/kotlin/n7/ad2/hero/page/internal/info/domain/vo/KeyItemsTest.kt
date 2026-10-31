package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuidePhase
import org.junit.Assert.assertEquals
import org.junit.Test

class KeyItemsTest {

    private fun item(name: String) = VOGuideItem(name, "url/$name")

    private fun guide(phases: List<VOGuidePhase>) = VOGuide(
        winrate = "",
        matches = "",
        proPicks = 0,
        proWinrate = null,
        proBans = 0,
        rankWinrates = emptyList(),
        durationWinrates = emptyList(),
        skillBuild = null,
        startingItems = listOf(item("tango")),
        phases = phases,
        easyToWin = emptyList(),
        hardToWin = emptyList(),
    )

    @Test
    fun `key items are the first mid game items, early ones are mostly boots and stats`() {
        val guide = guide(
            listOf(
                VOGuidePhase(VOGuidePhase.Phase.EARLY, listOf(item("treads"))),
                VOGuidePhase(VOGuidePhase.Phase.MID, listOf(item("bfury"), item("yasha"), item("manta"), item("diadem"), item("basher"))),
            ),
        )
        assertEquals(listOf(item("bfury"), item("yasha"), item("manta"), item("diadem")), guide.keyItems())
    }

    @Test
    fun `no mid game items, no key items`() {
        assertEquals(emptyList<VOGuideItem>(), guide(emptyList()).keyItems())
    }
}

package n7.ad2.parser.heroes

import java.io.File
import n7.ad2.parser.heroes.wiki.WikiHeroParser
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Real pages from dota2.fandom.com/(ru/)api.php?action=parse, saved in src/test/resources:
 *   wiki_antimage_en/ru.html  — Anti-Mage, the EN one has Lua errors that break the html nesting
 *   wiki_antimage_counters_en.html, wiki_antimage_guide_en/ru.html — subpages, the RU guide is mostly a template
 */
class WikiHeroParserTest {

    private fun doc(name: String) = Jsoup.parse(File("src/test/resources/$name"), "UTF-8")

    private val ru by lazy { WikiHeroParser.parseHeroPage(doc("wiki_antimage_ru.html")) }
    private val en by lazy { WikiHeroParser.parseHeroPage(doc("wiki_antimage_en.html")) }

    @Test
    fun `infobox and quote`() {
        assertEquals("Кто с магией к нам придёт, тот от моего клинка и погибнет.", ru.quote)
        assertEquals("They who live by the wand shall die by my blade.", en.quote)
        assertEquals("2010-11-01", ru.releaseDate)
        assertEquals("0,3 + 0,3", ru.attackAnimation)
        assertEquals("27", en.collisionRadius)
    }

    @Test
    fun `recommended items survive broken html`() {
        assertEquals(listOf("Starting items", "Early game", "Mid game", "Late game", "Situational items"), en.recommendedItems.map { it.title })
        val starting = en.recommendedItems.first().blocks
        assertEquals("Tango", starting.first().entity)
        assertTrue(starting.first().lines.single().startsWith("Tango provides"))
        // the RU page has the heading but no items
        assertTrue(ru.recommendedItems.isEmpty())
    }

    @Test
    fun `ability notes with nested notes and tags`() {
        val blink = ru.abilityNotes.getValue("blink").flatMap { it.notes }
        assertEquals("Blink можно использовать для избегания летящих снарядов.", blink.first().text)
        assertEquals(1, blink.count { it.indent == 1 })

        val counterspell = ru.abilityNotes.getValue("counterspell").flatMap { it.notes }
        assertTrue(counterspell.any { it.tag == "ПРИМЕЧАНИЯ К AGHANIM'S SHARD" })

        // cosmetic notes are about paid items, not about the ability
        (ru.abilityNotes.values + en.abilityNotes.values).flatten().flatMap { it.notes }.forEach { note ->
            assertFalse(note.text, note.text.contains("Disciple's Path"))
        }
        assertTrue(en.abilityNotes.getValue("mana void").isNotEmpty())
    }

    @Test
    fun `lua errors of the EN wiki never reach the text`() {
        val texts = en.abilityNotes.values.flatten().flatMap { it.notes }.map { it.text } +
            en.recommendedItems.flatMap { it.blocks }.flatMap { it.lines }
        assertTrue(texts.size > 30)
        texts.forEach { text ->
            assertFalse(text, text.contains("Lua error") || text.contains("scribunto") || text.contains("(/)"))
        }
    }

    @Test
    fun `counters are grouped by hero`() {
        val counters = WikiHeroParser.parseCounters(doc("wiki_antimage_counters_en.html"))
        val bad = counters.first()
        assertEquals("Bad against...", bad.title)
        assertEquals("Axe", bad.blocks.first().entity)
        assertTrue(bad.blocks.first().lines.first().startsWith("Berserker's Call prevents"))
        assertTrue(counters.any { it.title == "Works well with..." })
        counters.flatMap { it.blocks }.flatMap { it.lines }.forEach { assertFalse(it, it.contains("▶")) }
    }

    @Test
    fun `guide playstyle and tips`() {
        val en = WikiHeroParser.parseGuide(doc("wiki_antimage_guide_en.html"))
        assertTrue(en.playstyle!!.startsWith("Anti-Mage is one of the most mobile carries"))
        assertEquals(6, en.pros.size)
        assertEquals("Extremely Squishy.", en.cons.first())
        assertEquals(listOf("Carry", "General", "Mana Break", "Blink", "Counterspell", "Mana Void"), en.tips.map { it.title })

        val ru = WikiHeroParser.parseGuide(doc("wiki_antimage_guide_ru.html"))
        assertTrue(ru.playstyle!!.startsWith("Anti-Mage — убийца магов"))
        // only template headings like "СТИЛЬ1" and "Способность 1" are left there
        assertTrue(ru.tips.isEmpty())
        assertNull(ru.tips.firstOrNull())
    }
}

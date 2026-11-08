package n7.ad2.parser.heroes

import java.io.File
import n7.ad2.parser.heroes.wiki.WikiHeroParser
import n7.ad2.parser.heroes.wiki.WikiJson
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
    fun `a block the RU wiki does not have is left out, never taken from EN`() {
        val json = WikiJson.build(WikiJson.Pages(ru, counters = emptyList(), guide = null))
        assertNull(json["recommendedItems"])
        assertNull(json["counters"])
        assertNull(json["guide"])
        val notes = WikiJson.abilityNotes("blink", WikiJson.Pages(ru, emptyList(), null))!!
        assertEquals("Blink можно использовать для избегания летящих снарядов.", (notes.first() as org.json.simple.JSONObject)["text"])
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

    // the RU counters pages are mostly the unfilled template: "Причина"/"Запись" bullets and no hero names
    @Test
    fun `template placeholders of the RU counters page are dropped with the sections left empty`() {
        val html = """
            <h2><span class="mw-headline">Плох против</span></h2>
            <ul><li>Juxtapose бьёт по Doom</li><li>Причина</li><li>Причина</li></ul>
            <h3><span class="mw-headline">Другое</span></h3>
            <ul><li>Запись</li><li>Запись</li></ul>
            <h2><span class="mw-headline">Хорош против</span></h2>
            <ul><li>Reason</li><li>Entry</li></ul>
        """.trimIndent()
        val counters = WikiHeroParser.parseCounters(Jsoup.parse(html))
        assertEquals(listOf("Плох против"), counters.map { it.title })
        assertEquals(listOf("Juxtapose бьёт по Doom"), counters.single().blocks.single().lines)
    }

    @Test
    fun `recommended items have no empty or repeated entries and never take a file link for the item`() {
        val html = """
            <h2><span class="mw-headline">Recommended Items</span></h2>
            <p><b>Starting items:</b></p>
            <ul>
            <li></li>
            <li><a href="/wiki/Iron_Branch" title="Iron Branch"><img alt="Iron Branch"></a> <a href="/wiki/Iron_Branch" title="Iron Branch">Iron Branch</a> is cheap.</li>
            <li><a href="/wiki/Iron_Branch" title="Iron Branch"><img alt="Iron Branch"></a> <a href="/wiki/Iron_Branch" title="Iron Branch">Iron Branch</a> is cheap.</li>
            <li><a href="/wiki/File:Ultimate_Scepter_icon.png" title="File:Ultimate Scepter icon.png"><img alt=""></a>
            <a href="/wiki/Aghanim%27s_Scepter" title="Aghanim's Scepter">Aghanim's Scepter</a> adds damage.</li>
            </ul>
            <h2><span class="mw-headline">Abilities</span></h2>
        """.trimIndent()
        val items = WikiHeroParser.parseHeroPage(Jsoup.parse(html)).recommendedItems.single()
        assertEquals("Starting items", items.title)
        assertEquals(
            listOf(WikiHeroParser.Block("Iron Branch", listOf("Iron Branch is cheap.")), WikiHeroParser.Block("Aghanim's Scepter", listOf("Aghanim's Scepter adds damage."))),
            items.blocks,
        )
    }

    // the EN wiki has a vandal's "Regular Stick" for Magic Stick ("provides nothing"), and both wikis still advise items removed from the game
    @Test
    fun `recommended items keep only items the game has`() {
        val assets = java.nio.file.Files.createTempDirectory("assets").toFile().apply {
            File(this, "items/tango").mkdirs()
            File(this, "items/tango/full.webp").writeText("webp")
        }
        val section = WikiHeroParser.Section(
            title = "Starting items",
            level = 3,
            blocks = listOf(
                WikiHeroParser.Block("Regular Stick", listOf("Regular Stick provides nothing.")),
                WikiHeroParser.Block("Tango (не свой)", listOf("Tango from an ally.")),
                WikiHeroParser.Block("Stout Shield", listOf("Stout Shield blocks damage.")),
                WikiHeroParser.Block(null, listOf("Buy what the team lacks.")),
            ),
        )
        val page = WikiHeroParser.HeroPage(recommendedItems = listOf(section), abilityNotes = emptyMap())

        val json = WikiJson.build(WikiJson.Pages(page, counters = emptyList(), guide = null), assets)

        val entries = ((json["recommendedItems"] as org.json.simple.JSONArray).single() as org.json.simple.JSONObject)["entries"] as org.json.simple.JSONArray
        assertEquals(listOf("Tango (не свой)", null), entries.map { (it as org.json.simple.JSONObject)["name"] })
        assertEquals("items/tango/full.webp", (entries.first() as org.json.simple.JSONObject)["icon"])
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

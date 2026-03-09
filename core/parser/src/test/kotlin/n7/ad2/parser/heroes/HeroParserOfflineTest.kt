package n7.ad2.parser.heroes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

class HeroParserOfflineTest {

    companion object {
        private lateinit var heroesDoc: Document
        private lateinit var pudgeDoc: Document

        @JvmStatic
        @BeforeClass
        fun setup() {
            heroesDoc = loadFixture(
                filename = "heroes_list.html",
                url = "https://dota2.fandom.com/wiki/Heroes",
            )
            pudgeDoc = loadFixture(
                filename = "pudge.html",
                url = "https://dota2.fandom.com/wiki/Pudge",
            )
        }

        /**
         * Loads an HTML fixture from disk if it exists, otherwise downloads it via Jsoup
         * and caches it for offline use on subsequent runs.
         *
         * To manually populate fixtures (e.g. if Cloudflare blocks the download),
         * save the page HTML to core/parser/src/test/resources/<filename> using a browser.
         */
        private fun loadFixture(filename: String, url: String): Document {
            val file = File("src/test/resources/$filename")
            if (file.exists() && file.length() > 10_000) {
                return Jsoup.parse(file, "UTF-8")
            }
            println("Downloading fixture $filename from $url ...")
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .timeout(30_000)
                .get()
            file.parentFile.mkdirs()
            file.writeText(doc.outerHtml())
            println("Saved ${file.length()} bytes to ${file.path}")
            return doc
        }
    }

    // ===== Heroes List Tests =====

    @Test
    fun `heroes list has all four attribute groups`() {
        val heroes = parseHeroesList(heroesDoc)
        assertTrue("No Strength heroes", heroes.any { it.mainAttribute == "Strength" })
        assertTrue("No Agility heroes", heroes.any { it.mainAttribute == "Agility" })
        assertTrue("No Intelligence heroes", heroes.any { it.mainAttribute == "Intelligence" })
        assertTrue("No Universal heroes", heroes.any { it.mainAttribute == "Universal" })
    }

    @Test
    fun `heroes list has at least 100 heroes`() {
        val heroes = parseHeroesList(heroesDoc)
        assertTrue("Got ${heroes.size} heroes", heroes.size >= 100)
    }

    @Test
    fun `hero names have no Category or File prefixes`() {
        parseHeroesList(heroesDoc).forEach { hero ->
            assertFalse("Category: in name: ${hero.name}", hero.name.contains("Category:"))
            assertFalse("File: in name: ${hero.name}", hero.name.contains("File:"))
        }
    }

    // ===== Basic Info Tests =====

    @Test
    fun `pudge basic info`() {
        val info = parseBasicInfo(pudgeDoc, "Strength")
        assertEquals("Strength", info.mainAttribute)
        assertEquals("Melee", info.attackType)
        assertTrue("Roles empty", info.roles.isNotEmpty())
        assertTrue("Complexity out of range: ${info.complexity}", info.complexity in 1..3)
    }

    // ===== Description Test =====

    @Test
    fun `pudge description not empty`() {
        val desc = parseDescription(pudgeDoc)
        assertTrue("Description too short: '$desc'", desc.length > 20)
    }

    // ===== Main Attributes Tests =====

    @Test
    fun `pudge main attributes positive`() {
        val a = parseMainAttributes(pudgeDoc)
        assertTrue("Strength: ${a.attrStrength}", a.attrStrength > 0)
        assertTrue("StrengthInc: ${a.attrStrengthInc}", a.attrStrengthInc > 0)
        assertTrue("Agility: ${a.attrAgility}", a.attrAgility > 0)
        assertTrue("Intelligence: ${a.attrIntelligence}", a.attrIntelligence > 0)
    }

    // ===== Base Stats Tests =====

    @Test
    fun `pudge base stats`() {
        val s = parseBaseStats(pudgeDoc)
        assertTrue("Movement speed: ${s.movementSpeed}", s.movementSpeed > 0)
        // Pudge has negative armor at base; just check it was parsed (not default 0.0 from empty)
        assertTrue("Damage empty", s.damage.isNotEmpty())
    }

    // ===== Abilities Tests =====

    @Test
    fun `pudge has at least 5 abilities`() {
        val abilities = parseAbilities(pudgeDoc)
        // Pudge: Meat Hook, Rot, Flesh Heap, Eject, Dismember
        assertTrue("Got ${abilities.size} abilities", abilities.size >= 5)
    }

    @Test
    fun `all pudge abilities have valid names`() {
        parseAbilities(pudgeDoc).forEach { a ->
            assertTrue("Empty name", a.name.isNotEmpty())
            assertFalse("HTML in name: ${a.name}", a.name.startsWith("<"))
        }
    }

    @Test
    fun `pudge meat hook has effects`() {
        val hook = parseAbilities(pudgeDoc).firstOrNull { it.name == "Meat Hook" }
        assertNotNull("Meat Hook not found", hook)
        assertTrue("No effects", hook!!.effects.isNotEmpty())
    }

    @Test
    fun `pudge has aghanim scepter upgrade on at least one ability`() {
        val abilities = parseAbilities(pudgeDoc)
        val withScepter = abilities.filter { it.aghanimScepter != null }
        assertTrue("No ability has Aghanim Scepter upgrade", withScepter.isNotEmpty())
    }

    // ===== Talents Tests =====

    @Test
    fun `pudge has 4 talent rows`() {
        val talents = parseTalents(pudgeDoc)
        assertEquals("Got ${talents.size} talent rows", 4, talents.size)
        talents.forEach { t ->
            assertTrue("Talent level empty", t.talentLvl.isNotEmpty())
            assertTrue("Talent left empty", t.talentLeft.isNotEmpty())
            assertTrue("Talent right empty", t.talentRight.isNotEmpty())
        }
    }
}

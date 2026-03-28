package n7.ad2.parser.heroes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

/**
 * Offline unit tests for HeroParser.
 *
 * Requires HTML fixtures in core/parser/src/test/resources/:
 *   - heroes_list.html  (https://dota2.fandom.com/wiki/Heroes)
 *   - pudge.html        (https://dota2.fandom.com/wiki/Pudge)
 *
 * Save each page via your browser ("Save as → Webpage, Complete" or just the HTML source).
 * If fixtures are missing all tests are skipped (not failed).
 */
class HeroParserOfflineTest {

    companion object {
        private var heroesDoc: Document? = null
        private var pudgeDoc: Document? = null
        private var fixturesLoaded = false

        @JvmStatic
        @BeforeClass
        fun setup() {
            heroesDoc = tryLoadFixture("heroes_list.html")
            pudgeDoc = tryLoadFixture("pudge.html")
            fixturesLoaded = heroesDoc != null && pudgeDoc != null
            if (!fixturesLoaded) {
                println(
                    "\nSKIPPING offline tests — fixtures not found.\n" +
                        "To enable: save the wiki pages to core/parser/src/test/resources/\n" +
                        "  heroes_list.html → https://dota2.fandom.com/wiki/Heroes\n" +
                        "  pudge.html       → https://dota2.fandom.com/wiki/Pudge\n",
                )
            }
        }

        private fun tryLoadFixture(filename: String): Document? {
            val file = File("src/test/resources/$filename")
            if (file.exists() && file.length() > 10_000) {
                return Jsoup.parse(file, "UTF-8")
            }
            return null
        }
    }

    private fun requireFixtures() = assumeTrue("HTML fixtures not present — skipping", fixturesLoaded)

    // ===== Heroes List Tests =====

    @Test
    fun `heroes list has all four attribute groups`() {
        requireFixtures()
        val heroes = parseHeroesList(heroesDoc!!)
        assertTrue("No Strength heroes", heroes.any { it.mainAttribute == "Strength" })
        assertTrue("No Agility heroes", heroes.any { it.mainAttribute == "Agility" })
        assertTrue("No Intelligence heroes", heroes.any { it.mainAttribute == "Intelligence" })
        assertTrue("No Universal heroes", heroes.any { it.mainAttribute == "Universal" })
    }

    @Test
    fun `heroes list has at least 100 heroes`() {
        requireFixtures()
        val heroes = parseHeroesList(heroesDoc!!)
        assertTrue("Got ${heroes.size} heroes", heroes.size >= 100)
    }

    @Test
    fun `hero names have no Category or File prefixes`() {
        requireFixtures()
        parseHeroesList(heroesDoc!!).forEach { hero ->
            assertFalse("Category: in name: ${hero.name}", hero.name.contains("Category:"))
            assertFalse("File: in name: ${hero.name}", hero.name.contains("File:"))
        }
    }

    // ===== Basic Info Tests =====

    @Test
    fun `pudge basic info`() {
        requireFixtures()
        val info = parseBasicInfo(pudgeDoc!!, "Strength")
        assertEquals("Strength", info.mainAttribute)
        assertEquals("Melee", info.attackType)
        assertTrue("Roles empty", info.roles.isNotEmpty())
        assertTrue("Complexity out of range: ${info.complexity}", info.complexity in 1..3)
    }

    // ===== Description Test =====

    @Test
    fun `pudge description not empty`() {
        requireFixtures()
        val desc = parseDescription(pudgeDoc!!)
        assertTrue("Description too short: '$desc'", desc.length > 20)
    }

    // ===== Main Attributes Tests =====

    @Test
    fun `pudge main attributes positive`() {
        requireFixtures()
        val a = parseMainAttributes(pudgeDoc!!)
        assertTrue("Strength: ${a.attrStrength}", a.attrStrength > 0)
        assertTrue("StrengthInc: ${a.attrStrengthInc}", a.attrStrengthInc > 0)
        assertTrue("Agility: ${a.attrAgility}", a.attrAgility > 0)
        assertTrue("Intelligence: ${a.attrIntelligence}", a.attrIntelligence > 0)
    }

    // ===== Base Stats Tests =====

    @Test
    fun `pudge base stats`() {
        requireFixtures()
        val s = parseBaseStats(pudgeDoc!!)
        assertTrue("Movement speed: ${s.movementSpeed}", s.movementSpeed > 0)
        assertTrue("Damage empty", s.damage.isNotEmpty())
    }

    // ===== Abilities Tests =====

    @Test
    fun `pudge has at least 5 abilities`() {
        requireFixtures()
        val abilities = parseAbilities(pudgeDoc!!)
        assertTrue("Got ${abilities.size} abilities", abilities.size >= 5)
    }

    @Test
    fun `all pudge abilities have valid names`() {
        requireFixtures()
        parseAbilities(pudgeDoc!!).forEach { a ->
            assertTrue("Empty name", a.name.isNotEmpty())
            assertFalse("HTML in name: ${a.name}", a.name.startsWith("<"))
        }
    }

    @Test
    fun `pudge meat hook has effects`() {
        requireFixtures()
        val hook = parseAbilities(pudgeDoc!!).firstOrNull { it.name == "Meat Hook" }
        assertNotNull("Meat Hook not found", hook)
        assertTrue("No effects", hook!!.effects.isNotEmpty())
    }

    @Test
    fun `pudge has aghanim scepter upgrade on at least one ability`() {
        requireFixtures()
        val abilities = parseAbilities(pudgeDoc!!)
        val withScepter = abilities.filter { it.aghanimScepter != null }
        assertTrue("No ability has Aghanim Scepter upgrade", withScepter.isNotEmpty())
    }

    // ===== Talents Tests =====

    @Test
    fun `pudge has 4 talent rows`() {
        requireFixtures()
        val talents = parseTalents(pudgeDoc!!)
        assertEquals("Got ${talents.size} talent rows", 4, talents.size)
        talents.forEach { t ->
            assertTrue("Talent level empty", t.talentLvl.isNotEmpty())
            assertTrue("Talent left empty", t.talentLeft.isNotEmpty())
            assertTrue("Talent right empty", t.talentRight.isNotEmpty())
        }
    }
}

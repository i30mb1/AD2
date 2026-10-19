package n7.ad2.parser.heroes

import java.io.File
import n7.ad2.parser.heroes.valve.ValveHeroParser
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Offline tests on real responses saved in src/test/resources:
 *   - valve_abaddon_ru.json      https://www.dota2.com/datafeed/herodata?language=russian&hero_id=102
 *   - wiki_lion_*_trivia.html    Trivia section of https://dota2.fandom.com/(ru/)api.php?action=parse&page=Lion
 */
class HeroParserOfflineTest {

    private val abaddon: JSONObject by lazy {
        val root = JSONParser().parse(File("src/test/resources/valve_abaddon_ru.json").readText()) as JSONObject
        val heroes = ((root["result"] as JSONObject)["data"] as JSONObject)["heroes"] as JSONArray
        ValveHeroParser.parse(heroes.first() as JSONObject) { key, _ -> "images/abilities/$key.webp" }
    }

    private fun ability(name: String): JSONObject = (abaddon["abilities"] as JSONArray)
        .filterIsInstance<JSONObject>()
        .first { it["name"] == name }

    private fun param(ability: JSONObject, label: String): JSONObject = (ability["params"] as JSONArray)
        .filterIsInstance<JSONObject>()
        .first { it["label"] == label }

    @Test
    fun `hero summary`() {
        assertEquals(ValveHeroParser.SCHEMA_VERSION, abaddon["schema"])
        assertEquals("Abaddon", abaddon["name"])
        assertEquals("universal", abaddon["primaryAttribute"])
        assertEquals("melee", abaddon["attackType"])
        assertEquals(1L, abaddon["complexity"])
        val roles = (abaddon["roles"] as JSONArray).filterIsInstance<JSONObject>().associate { it["role"] to it["level"] }
        assertEquals(mapOf("carry" to 1L, "support" to 2L, "durable" to 2L), roles)
        assertTrue((abaddon["bio"] as String).length > 100)
        assertTrue((abaddon["hype"] as String).contains("<b>"))
    }

    @Test
    fun `stats are taken as is`() {
        val stats = abaddon["stats"] as JSONObject
        assertEquals(582.0, stats["health"])
        assertEquals(303.0, stats["mana"])
        assertEquals(2.7, stats["armor"])
        assertEquals(22.0, stats["damageMin"])
        assertEquals(32.0, stats["damageMax"])
        assertEquals(325.0, stats["moveSpeed"])
    }

    @Test
    fun `ability kinds`() {
        assertEquals("basic", ability("Mist Coil")["kind"])
        assertEquals("innate", ability("Withering Mist")["kind"])
        assertEquals("ultimate", ability("Borrowed Time")["kind"])
        assertEquals("images/abilities/abaddon_death_coil.webp", ability("Mist Coil")["image"])
    }

    @Test
    fun `ability header values`() {
        val coil = ability("Mist Coil")
        assertEquals("8/7/6/5", coil["cooldown"])
        assertEquals("50/55/60/65", coil["manaCost"])
        assertEquals("625", coil["castRange"])
        assertEquals("magical", coil["damageType"])
        assertEquals("allies_yes_enemies_no", coil["spellImmunity"])
        assertEquals(listOf("unit_target"), coil["behavior"])
        assertNull(coil["channelTime"])
    }

    @Test
    fun `placeholders in descriptions are resolved`() {
        val mist = ability("Withering Mist")["description"] as String
        assertTrue(mist, mist.contains("5 сек."))
        assertTrue(mist, mist.contains("29.5%"))
        assertTrue(mist, mist.contains("40%"))
        assertFalse(mist, mist.contains("%%"))
        assertFalse(mist, Regex("%[a-z_]+%").containsMatchIn(mist))
    }

    @Test
    fun `aghanim upgrades use their own values`() {
        assertTrue(ability("Aphotic Shield")["shard"] as String, (ability("Aphotic Shield")["shard"] as String).contains("75%"))
        assertTrue((ability("Borrowed Time")["scepter"] as String).contains("550"))
        assertNull(ability("Mist Coil")["scepter"])
    }

    @Test
    fun `params with talent bonus`() {
        val damage = param(ability("Mist Coil"), "УРОН/ЛЕЧЕНИЕ")
        assertEquals("95/170/245/320", damage["value"])
        assertEquals("+35", damage["talent"])
        assertEquals("40%", param(ability("Mist Coil"), "УРОН СЕБЕ")["value"])
        assertEquals("3", param(ability("Borrowed Time"), "ДЛИТЕЛЬНОСТЬ")["scepter"])
    }

    @Test
    fun `talents are paired by level`() {
        val talents = (abaddon["talents"] as JSONArray).filterIsInstance<JSONObject>()
        assertEquals(listOf(10L, 15L, 20L, 25L), talents.map { (it["level"] as Number).toLong() })
        assertEquals("+30 к урону в секунду от Curse of Avernus", talents[0]["right"])
        assertEquals("Aphotic Shield даёт +8 здоровья в секунду", talents[0]["left"])
        assertEquals("–10 сек. перезарядки Borrowed Time", talents[1]["right"])
        assertEquals("Mist Coil применяется по области радиусом 350", talents[3]["left"])
        talents.forEach { talent -> assertFalse(talent.toString(), talent.toString().contains("{s:")) }
    }

    @Test
    fun `trivia collects every list and drops audio buttons`() {
        val en = parseTrivia(Jsoup.parse(File("src/test/resources/wiki_lion_en_trivia.html"), "UTF-8"))
        assertEquals(2, en.size)
        assertTrue(en[0].startsWith("In the transition from DotA to Dota 2"))
        en.forEach { assertFalse(it, it.contains("Link") || it.contains("▶")) }

        val ru = parseTrivia(Jsoup.parse(File("src/test/resources/wiki_lion_ru_trivia.html"), "UTF-8"))
        assertTrue(ru.isNotEmpty())
        assertTrue(ru[0].startsWith("При переходе из DotA в Dota 2"))
    }
}

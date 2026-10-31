package n7.ad2.heroes.domain.internal.data

import kotlinx.serialization.json.Json
import n7.ad2.heroes.domain.internal.data.model.OpenDotaDuration
import n7.ad2.heroes.domain.model.SkillUpgrade
import n7.ad2.heroes.domain.internal.data.model.OpenDotaHeroStat
import n7.ad2.heroes.domain.internal.data.model.OpenDotaItemPopularity
import n7.ad2.heroes.domain.internal.data.model.OpenDotaMatchup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenDotaGuideMapperTest {

    private val json = Json { ignoreUnknownKeys = true }

    // Abaddon: bracket 1 55.0%, bracket 3 40.0%, together 750/1500 -> 50.0%. Axe & Bane only need id + name for the matchup map.
    private val statsJson = """
        [
          {"id":102,"name":"npc_dota_hero_abaddon","localized_name":"Abaddon","1_pick":1000,"1_win":550,"3_pick":500,"3_win":200,"pro_pick":12,"pro_win":9,"pro_ban":4},
          {"id":2,"name":"npc_dota_hero_axe","localized_name":"Axe"},
          {"id":3,"name":"npc_dota_hero_bane","localized_name":"Bane"},
          {"id":1,"name":"npc_dota_hero_antimage","localized_name":"Anti-Mage"}
        ]
    """.trimIndent()

    // vs Axe: 600/1000 = 60% (easy). vs Bane: 400/1000 = 40% (hard). vs Anti-Mage: too few games -> filtered out.
    private val matchupsJson = """
        [
          {"hero_id":2,"games_played":1000,"wins":600},
          {"hero_id":3,"games_played":1000,"wins":400},
          {"hero_id":1,"games_played":50,"wins":40}
        ]
    """.trimIndent()

    private val itemPopularityJson = """
        {
          "start_game_items": {"44":100,"16":80},
          "early_game_items": {"63":70},
          "mid_game_items": {"48":60},
          "late_game_items": {"116":50}
        }
    """.trimIndent()

    private val itemIdsJson = """
        {"44":"tango","16":"branches","63":"power_treads","48":"travel_boots","116":"assault"}
    """.trimIndent()

    // bins are seconds: 5 and 15 min fall into "up to 20", 25 into 20-30, 45 has too few games and is dropped
    private val durationsJson = """
        [
          {"duration_bin":300,"games_played":40,"wins":20},
          {"duration_bin":900,"games_played":60,"wins":20},
          {"duration_bin":1500,"games_played":100,"wins":60},
          {"duration_bin":2700,"games_played":10,"wins":10}
        ]
    """.trimIndent()

    private val q = "abaddon_death_coil"
    private val w = "abaddon_aphotic_shield"
    private val e = "abaddon_frostmourne"
    private val r = "abaddon_borrowed_time"
    private val t = "special_bonus_unique_abaddon"

    // the second build matches the per-level majority best, so it is the one shown
    private val upgradeSequences = listOf(
        listOf(w, w, w, q, q, r, t),
        listOf(q, w, q, w, q, r, t),
        listOf(q, w, q, e, q, r, t),
        listOf(q, e, q, w, q, r, e),
    )

    private fun buildGuide(heroName: String, sequences: List<List<String>> = upgradeSequences) = OpenDotaGuideMapper.toGuide(
        heroName = heroName,
        stats = json.decodeFromString<List<OpenDotaHeroStat>>(statsJson),
        matchups = json.decodeFromString<List<OpenDotaMatchup>>(matchupsJson),
        itemPopularity = json.decodeFromString<OpenDotaItemPopularity>(itemPopularityJson),
        itemIds = json.decodeFromString<Map<String, String>>(itemIdsJson),
        durations = json.decodeFromString<List<OpenDotaDuration>>(durationsJson),
        upgradeSequences = sequences,
    )

    @Test
    fun `maps winrate from skill-bracket totals`() {
        val guide = buildGuide("abaddon")!!
        assertEquals("Abaddon", guide.heroName)
        assertEquals("50.0%", guide.heroWinrate)
    }

    @Test
    fun `easy matchups are highest winrate, hard are lowest, low-sample filtered out`() {
        val guide = buildGuide("abaddon")!!
        assertEquals("Axe", guide.easyToWinHeroList.first().heroName)
        assertEquals(60.0, guide.easyToWinHeroList.first().heroWinrate, 0.001)
        assertEquals("Bane", guide.hardToWinHeroList.first().heroName)
        // Anti-Mage had only 50 games, below the threshold, so it is excluded entirely.
        assertTrue(guide.easyToWinHeroList.none { it.heroName == "Anti-Mage" })
        assertTrue(guide.hardToWinHeroList.none { it.heroName == "Anti-Mage" })
    }

    @Test
    fun `a hero is never both easy and hard to win against`() {
        val guide = buildGuide("abaddon")!!
        // with only two matchups the old top/bottom split put both heroes into both lists
        assertEquals(listOf("Axe"), guide.easyToWinHeroList.map { it.heroName })
        assertEquals(listOf("Bane"), guide.hardToWinHeroList.map { it.heroName })
    }

    @Test
    fun `counter avatars point at bundled webp assets`() {
        val guide = buildGuide("abaddon")!!
        assertEquals("file:///android_asset/heroes/axe/full.webp", guide.easyToWinHeroList.first().avatarUrl)
    }

    @Test
    fun `item ids are resolved to internal names`() {
        val guide = buildGuide("abaddon")!!
        assertTrue(guide.heroStartingHeroItemsList.map { it.itemName }.contains("tango"))
        assertTrue(guide.heroItemsList.map { it.itemName }.contains("power_treads"))
    }

    @Test
    fun `winrate per rank skips brackets without games`() {
        val guide = buildGuide("abaddon")!!
        assertEquals(listOf(1, 3), guide.rankWinrates.map { it.rank })
        assertEquals(55.0, guide.rankWinrates[0].winrate, 0.001)
        assertEquals(40.0, guide.rankWinrates[1].winrate, 0.001)
    }

    @Test
    fun `pro picks wins and bans are passed through`() {
        val guide = buildGuide("abaddon")!!
        assertEquals(12, guide.proPicks)
        assertEquals(9, guide.proWins)
        assertEquals(4, guide.proBans)
    }

    @Test
    fun `durations are grouped by ten minutes and thin groups dropped`() {
        val guide = buildGuide("abaddon")!!
        assertEquals(listOf(0, 20), guide.durationWinrates.map { it.fromMinute })
        assertEquals(40.0, guide.durationWinrates[0].winrate, 0.001)
        assertEquals(60.0, guide.durationWinrates[1].winrate, 0.001)
    }

    @Test
    fun `skill build is the real build closest to what most players take`() {
        val guide = buildGuide("abaddon")!!
        assertEquals(listOf(q, w, q, w, q, r, t), guide.skillBuild.map { it.abilityKey })
        assertEquals(SkillUpgrade.Kind.TALENT, guide.skillBuild.last().kind)
        assertTrue(guide.skillBuild.first().imageUrl.endsWith("abaddon_death_coil.png"))
    }

    @Test
    fun `attribute points are not talents`() {
        val guide = buildGuide("abaddon", sequences = listOf(listOf(q, "special_bonus_attributes", t)))!!
        assertEquals(listOf(SkillUpgrade.Kind.ABILITY, SkillUpgrade.Kind.ATTRIBUTES, SkillUpgrade.Kind.TALENT), guide.skillBuild.map { it.kind })
    }

    @Test
    fun `skill build is empty without parsed matches`() {
        assertTrue(buildGuide("abaddon", sequences = emptyList())!!.skillBuild.isEmpty())
    }

    @Test
    fun `matches by display name and by npc internal name`() {
        assertNotNull(buildGuide("Abaddon"))
        assertNotNull(buildGuide("abaddon"))
    }

    @Test
    fun `unknown hero yields null`() {
        assertNull(buildGuide("not_a_hero"))
    }
}

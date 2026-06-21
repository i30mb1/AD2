package n7.ad2.heroes.domain.internal.data

import kotlinx.serialization.json.Json
import n7.ad2.heroes.domain.internal.data.model.OpenDotaHeroAbilities
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

    // Abaddon: 1000 picks / 550 wins in bracket 1 -> 55.0%. Axe & Bane only need id + name for the matchup map.
    private val statsJson = """
        [
          {"id":102,"name":"npc_dota_hero_abaddon","localized_name":"Abaddon","1_pick":1000,"1_win":550},
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

    private val abilitiesJson = """
        {"npc_dota_hero_abaddon":{"abilities":["abaddon_death_coil","abaddon_aphotic_shield","abaddon_frostmourne","abaddon_borrowed_time","special_bonus_unique_abaddon"]}}
    """.trimIndent()

    private fun buildGuide(heroName: String) = OpenDotaGuideMapper.toGuide(
        heroName = heroName,
        stats = json.decodeFromString<List<OpenDotaHeroStat>>(statsJson),
        matchups = json.decodeFromString<List<OpenDotaMatchup>>(matchupsJson),
        itemPopularity = json.decodeFromString<OpenDotaItemPopularity>(itemPopularityJson),
        itemIds = json.decodeFromString<Map<String, String>>(itemIdsJson),
        abilities = json.decodeFromString<Map<String, OpenDotaHeroAbilities>>(abilitiesJson)
            .getValue("npc_dota_hero_abaddon").abilities,
    )

    @Test
    fun `maps winrate from skill-bracket totals`() {
        val guide = buildGuide("abaddon")!!
        assertEquals("Abaddon", guide.heroName)
        assertEquals("55.0%", guide.heroWinrate)
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
    fun `spells drop talents and carry an ordered skill index`() {
        val guide = buildGuide("abaddon")!!
        assertEquals(4, guide.heroSpellsList.size)
        assertEquals("1", guide.heroSpellsList.first().spellOrder)
        assertTrue(guide.heroSpellsList.first().spellImageUrl.endsWith("abaddon_death_coil.png"))
        assertTrue(guide.heroSpellsList.none { it.spellImageUrl.contains("special_bonus") })
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

    // Monkey King really ships a nested array inside its `abilities` list in constants/hero_abilities.
    // A strict Map<String, List<String>> decode throws on it and wipes spells for every hero, so the
    // extraction must read leniently and keep only string ids.
    @Test
    fun `parseAbilities keeps string ids and tolerates a nested-array entry`() {
        val body = """
            {
              "npc_dota_hero_monkey_king": {
                "abilities": ["monkey_king_boundless_strike","monkey_king_tree_dance",["monkey_king_untransform","monkey_king_transfiguration"]]
              },
              "npc_dota_hero_abaddon": {
                "abilities": ["abaddon_death_coil","abaddon_aphotic_shield"]
              }
            }
        """.trimIndent()
        // The Monkey King nested array must not prevent reading another hero's abilities.
        assertEquals(
            listOf("abaddon_death_coil", "abaddon_aphotic_shield"),
            OpenDotaGuideMapper.parseAbilities(json, body, "npc_dota_hero_abaddon"),
        )
        // For Monkey King itself the nested-array entry is dropped and the string ids are kept.
        assertEquals(
            listOf("monkey_king_boundless_strike", "monkey_king_tree_dance"),
            OpenDotaGuideMapper.parseAbilities(json, body, "npc_dota_hero_monkey_king"),
        )
    }

    @Test
    fun `parseAbilities yields empty list for an unknown hero`() {
        assertEquals(emptyList<String>(), OpenDotaGuideMapper.parseAbilities(json, "{}", "npc_dota_hero_abaddon"))
    }
}

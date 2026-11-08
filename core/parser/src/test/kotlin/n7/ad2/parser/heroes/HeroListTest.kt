package n7.ad2.parser.heroes

import n7.ad2.parser.heroes.valve.ValveApi
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.Assert.assertEquals
import org.junit.Test

/** `heroes/list.json` is what the heroes screen shows: every hero of the game with the attribute it has now. */
class HeroListTest {

    // the key is the game's name of a hero, OpenDota uses it too while its names lag ("Outworld Devourer")
    @Test
    fun `every hero with its attribute and key, by name`() {
        val heroes = listOf(
            ValveApi.HeroListItem(id = 1, name = "Anti-Mage", key = "npc_dota_hero_antimage", attribute = 1),
            ValveApi.HeroListItem(id = 155, name = "Largo", key = "npc_dota_hero_largo", attribute = 0),
            ValveApi.HeroListItem(id = 102, name = "Abaddon", key = "npc_dota_hero_abaddon", attribute = 3),
            ValveApi.HeroListItem(id = 138, name = "Muerta", key = "npc_dota_hero_muerta", attribute = 2),
        )

        val list = (JSONParser().parse(HeroList.json(heroes)) as JSONArray).map { it as JSONObject }

        assertEquals(
            listOf(
                mapOf("name" to "Abaddon", "attribute" to "universal", "key" to "npc_dota_hero_abaddon"),
                mapOf("name" to "Anti-Mage", "attribute" to "agility", "key" to "npc_dota_hero_antimage"),
                mapOf("name" to "Largo", "attribute" to "strength", "key" to "npc_dota_hero_largo"),
                mapOf("name" to "Muerta", "attribute" to "intelligence", "key" to "npc_dota_hero_muerta"),
            ),
            list.map { hero -> mapOf("name" to hero["name"], "attribute" to hero["attribute"], "key" to hero["key"]) },
        )
    }

    // a hero new to the app gets the same pictures as the others: 256×144 art and the 32×32 minimap icon
    @Test
    fun `pictures of a new hero come from the Valve CDN by its short name`() {
        val kez = ValveApi.HeroListItem(id = 145, name = "Kez", key = "npc_dota_hero_kez", attribute = 1)
        assertEquals("kez", kez.folderName)
        assertEquals("https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/heroes/kez.png", ValveApi.heroArtUrl(kez))
        assertEquals("https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/heroes/icons/kez.png", ValveApi.heroIconUrl(kez))
    }
}

package n7.ad2.parser.heroes

import n7.ad2.parser.assetsDatabaseHeroes
import n7.ad2.parser.heroes.valve.ValveApi
import n7.ad2.parser.heroes.valve.ValveHeroParser
import n7.ad2.parser.saveFile
import org.json.simple.JSONArray
import org.json.simple.JSONObject

/**
 * `heroes/list.json`: every hero the app has data for, with the primary attribute the hero has now. The heroes screen
 * syncs its list with it, so a new hero or a hero that moved to another attribute shows up without a new database.
 * The key (npc_dota_hero_obsidian_destroyer) finds the hero in OpenDota, whose names lag behind the game.
 */
internal object HeroList {

    fun json(heroes: List<ValveApi.HeroListItem>): String = JSONArray().apply {
        heroes.sortedBy { it.name }.forEach { hero ->
            add(JSONObject().apply {
                put("name", hero.name)
                put("attribute", ValveHeroParser.attribute(hero.attribute.toLong()))
                put("key", hero.key)
            })
        }
    }.toJSONString()

    fun save(heroes: List<ValveApi.HeroListItem>): Boolean = saveFile(assetsDatabaseHeroes, "list.json", json(heroes))
}

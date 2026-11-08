package n7.ad2.heroes.domain.internal.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * An entry of `heroes/list.json` (core/parser): a hero the app has data for and the primary attribute it has now.
 * [key] is the game's name of the hero, e.g. npc_dota_hero_obsidian_destroyer for Outworld Destroyer.
 */
@Serializable
internal data class ListedHero(val name: String, val attribute: String, val key: String = "")

/** What the database lacks against the list: heroes added to the game and heroes that moved to another attribute. */
internal class HeroListChanges(val added: List<ListedHero>, val moved: List<ListedHero>)

private val json = Json { ignoreUnknownKeys = true }

internal fun parseHeroList(text: String): List<ListedHero> = json.decodeFromString(text)

/** [known] is the attribute of every hero in the database by name, the rows of 2023 have it capitalized. */
internal fun heroListChanges(known: Map<String, String>, listed: List<ListedHero>): HeroListChanges = HeroListChanges(
    added = listed.filter { hero -> hero.name !in known },
    moved = listed.filter { hero -> known[hero.name]?.equals(hero.attribute, ignoreCase = true) == false },
)

/** The app's name of every hero by its key, OpenDota still names some heroes the old way ("Outworld Devourer"). */
internal fun heroNamesByKey(listed: List<ListedHero>): Map<String, String> = listed.filter { it.key.isNotEmpty() }.associate { it.key to it.name }

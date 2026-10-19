package n7.ad2.parser.heroes.valve

import n7.ad2.parser.LocaleHeroes
import n7.ad2.parser.heroes.valve.ValveHeroParser.cleanText
import org.json.simple.JSONArray
import org.json.simple.JSONObject

/** Hero changes from the official patch notes, the wiki "Recent changes" stopped years ago. */
internal object ValvePatchNotes {

    private const val PATCHES_TO_SHOW = 5
    private const val PATCHES_TO_SEARCH = 30

    /**
     * Latest patches that changed the hero, newest first.
     * [abilityNames] maps the datafeed ability id to its localized name.
     */
    fun collect(heroId: Long, abilityNames: Map<Long, String>, locale: LocaleHeroes): JSONArray {
        val result = JSONArray()
        for (patch in ValveApi.patches.take(PATCHES_TO_SEARCH)) {
            if (result.size >= PATCHES_TO_SHOW) break
            val hero = try {
                (ValveApi.patchNotes(patch.name, locale)["heroes"] as? JSONArray)
                    ?.filterIsInstance<JSONObject>()
                    ?.firstOrNull { (it["hero_id"] as? Number)?.toLong() == heroId }
            } catch (e: Exception) {
                println("  WARNING: patch ${patch.name}: ${e.message}")
                null
            } ?: continue

            val heroNotes = notes(hero["hero_notes"])
            val talentNotes = notes(hero["talent_notes"])
            val abilities = (hero["abilities"] as? JSONArray).orEmpty().mapNotNull { ability ->
                val id = (ability["ability_id"] as? Number)?.toLong() ?: return@mapNotNull null
                val abilityNotes = notes(ability["ability_notes"])
                if (abilityNotes.isEmpty()) return@mapNotNull null
                JSONObject().apply {
                    put("name", abilityNames[id] ?: return@mapNotNull null)
                    put("notes", abilityNotes)
                }
            }
            if (heroNotes.isEmpty() && talentNotes.isEmpty() && abilities.isEmpty()) continue

            result.add(
                JSONObject().apply {
                    put("patch", patch.name)
                    put("timestamp", patch.timestamp)
                    put("hero", heroNotes)
                    put("talents", talentNotes)
                    put("abilities", JSONArray().apply { addAll(abilities) })
                },
            )
        }
        return result
    }

    private fun notes(value: Any?): JSONArray = JSONArray().apply {
        (value as? JSONArray).orEmpty().forEach { note ->
            val text = (note["note"] as? String)?.cleanText()?.takeIf { it.isNotEmpty() } ?: return@forEach
            add(
                JSONObject().apply {
                    put("text", text)
                    put("indent", ((note["indent_level"] as? Number)?.toInt() ?: 1) - 1)
                },
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun JSONArray?.orEmpty(): List<JSONObject> = (this as List<Any?>?)?.filterIsInstance<JSONObject>() ?: emptyList()
}

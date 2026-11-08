package n7.ad2.parser.heroes.wiki

import java.io.File
import n7.ad2.parser.assetsDatabase
import org.json.simple.JSONArray
import org.json.simple.JSONObject

/**
 * Builds the wiki part of `description.json` from the wiki of the file's language only: a block the wiki of that
 * language does not have is left out, the app never mixes languages.
 */
internal object WikiJson {

    class Pages(
        val hero: WikiHeroParser.HeroPage?,
        val counters: List<WikiHeroParser.Section>,
        val guide: WikiHeroParser.Guide?,
    )

    /**
     * [assets] is the assets folder of the app. Recommended items keep only the items the game has: the wikis still
     * advise removed ones (Stout Shield, Ring of Aquila) and the EN one has a vandal's "Regular Stick" for Magic Stick.
     */
    fun build(pages: Pages, assets: File = File(assetsDatabase)): JSONObject = JSONObject().apply {
        pages.hero?.recommendedItems
            ?.map { section -> section.copy(blocks = section.blocks.filter { block -> block.entity == null || itemIcon(block.entity, assets) != null }) }
            ?.filter { it.blocks.isNotEmpty() }
            ?.takeIf { it.isNotEmpty() }
            ?.let { sections -> put("recommendedItems", sectionsJson(sections, entityIcon = { itemIcon(it, assets) })) }
        // the RU counters pages are stubs without heroes, their general notes are still worth showing
        pages.counters.takeIf { it.isNotEmpty() }?.let { sections ->
            put("counters", sectionsJson(sections, entityIcon = { heroIcon(it, assets) ?: itemIcon(it, assets) }))
        }
        guideJson(pages.guide)?.let { put("guide", it) }
    }

    /** Notes of one ability, [name] in lower case. */
    fun abilityNotes(name: String, pages: Pages): JSONArray? {
        val sections = pages.hero?.abilityNotes?.get(name)?.takeIf { it.isNotEmpty() } ?: return null
        return JSONArray().apply {
            sections.forEach { section ->
                section.notes.forEach { note ->
                    add(JSONObject().apply {
                        put("text", note.text)
                        put("tag", note.tag ?: section.title)
                        put("indent", note.indent)
                    })
                }
            }
        }
    }

    private fun guideJson(guide: WikiHeroParser.Guide?): JSONObject? {
        if (guide == null || (guide.playstyle == null && guide.tips.isEmpty())) return null
        return JSONObject().apply {
            guide.playstyle?.let { text ->
                put("playstyle", JSONObject().apply {
                    put("text", text)
                    put("pros", JSONArray().apply { addAll(guide.pros) })
                    put("cons", JSONArray().apply { addAll(guide.cons) })
                })
            }
            if (guide.tips.isNotEmpty()) put("tips", sectionsJson(guide.tips, entityIcon = { null }))
        }
    }

    private fun sectionsJson(sections: List<WikiHeroParser.Section>, entityIcon: (String) -> String?): JSONArray = JSONArray().apply {
        sections.forEach { section ->
            add(JSONObject().apply {
                put("title", section.title.removeSuffix("...").removeSuffix("…").trim())
                put("level", section.level)
                put("entries", JSONArray().apply {
                    section.blocks.forEach { block ->
                        add(JSONObject().apply {
                            put("name", block.entity)
                            put("icon", block.entity?.let(entityIcon))
                            put("lines", JSONArray().apply { addAll(block.lines) })
                        })
                    }
                })
            })
        }
    }

    private fun String.assetKey() = lowercase().replace(" ", "_")

    private fun heroIcon(name: String, assets: File): String? = "heroes/${name.assetKey()}/minimap.webp".takeIf { File(assets, it).exists() }

    /** "Tango (не свой)" is a Tango from an ally. */
    private fun itemIcon(name: String, assets: File): String? =
        "items/${name.substringBefore(" (").assetKey()}/full.webp".takeIf { File(assets, it).exists() }
}

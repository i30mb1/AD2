package n7.ad2.parser.heroes.wiki

import java.io.File
import n7.ad2.parser.assetsDatabase
import org.json.simple.JSONArray
import org.json.simple.JSONObject

/**
 * Builds the wiki part of `description.json`. The RU wiki is often an empty template where the EN one is full,
 * so every block is taken from the page of the locale when it has content and from the EN page otherwise,
 * such blocks get `"lang": "en"`.
 */
internal object WikiJson {

    class Pages(
        val hero: WikiHeroParser.HeroPage?,
        val counters: List<WikiHeroParser.Section>,
        val guide: WikiHeroParser.Guide?,
    )

    /** A localized counters page with fewer heroes than this part of the EN one is a stub ("incomplete article"). */
    private const val ENOUGH_TRANSLATED = 0.5

    private fun List<WikiHeroParser.Section>.entryCount(): Int = sumOf { section -> section.blocks.count { it.entity != null } }

    fun build(local: Pages, english: Pages?, isEnglish: Boolean): JSONObject = JSONObject().apply {
        put("quote", local.hero?.quote)
        put("releaseDate", local.hero?.releaseDate ?: english?.hero?.releaseDate)
        put("attackAnimation", local.hero?.attackAnimation ?: english?.hero?.attackAnimation)
        put("collisionRadius", local.hero?.collisionRadius ?: english?.hero?.collisionRadius)

        pick(local.hero?.recommendedItems.orEmpty(), english?.hero?.recommendedItems.orEmpty(), isEnglish) { it.isNotEmpty() }
            ?.let { (sections, lang) -> put("recommendedItems", sectionsJson(sections, lang, entityIcon = ::itemIcon)) }

        val englishEntries = english?.counters.orEmpty().entryCount()
        pick(local.counters, english?.counters.orEmpty(), isEnglish) { counters ->
            counters.entryCount() > 0 && counters.entryCount() >= englishEntries * ENOUGH_TRANSLATED
        }
            ?.let { (sections, lang) -> put("counters", sectionsJson(sections, lang, entityIcon = { heroIcon(it) ?: itemIcon(it) })) }

        val guide = guideJson(local.guide, english?.guide, isEnglish)
        if (guide != null) put("guide", guide)
    }

    /** Notes of one ability, [name] in lower case. */
    fun abilityNotes(name: String, local: Pages, english: Pages?, isEnglish: Boolean): JSONObject? {
        val (notes, lang) = pick(
            local.hero?.abilityNotes?.get(name).orEmpty(),
            english?.hero?.abilityNotes?.get(name).orEmpty(),
            isEnglish,
        ) { it.isNotEmpty() } ?: return null
        return JSONObject().apply {
            put("lang", lang)
            put("notes", JSONArray().apply {
                notes.forEach { section ->
                    section.notes.forEach { note ->
                        add(JSONObject().apply {
                            put("text", note.text)
                            put("tag", note.tag ?: section.title)
                            put("indent", note.indent)
                        })
                    }
                }
            })
        }
    }

    private fun <T> pick(local: T, english: T, isEnglish: Boolean, isEnough: (T) -> Boolean): Pair<T, String>? = when {
        isEnough(local) -> local to if (isEnglish) "en" else "ru"
        !isEnglish && isEnough(english) -> english to "en"
        else -> null
    }

    private fun guideJson(local: WikiHeroParser.Guide?, english: WikiHeroParser.Guide?, isEnglish: Boolean): JSONObject? {
        val playstyle = pick(local, english, isEnglish) { it?.playstyle != null }?.let { (guide, lang) -> guide!! to lang }
        val tips = pick(local?.tips.orEmpty(), english?.tips.orEmpty(), isEnglish) { it.isNotEmpty() }
        if (playstyle == null && tips == null) return null
        return JSONObject().apply {
            if (playstyle != null) {
                val (guide, lang) = playstyle
                put("playstyle", JSONObject().apply {
                    put("lang", lang)
                    put("text", guide.playstyle)
                    put("pros", JSONArray().apply { addAll(guide.pros) })
                    put("cons", JSONArray().apply { addAll(guide.cons) })
                })
            }
            if (tips != null) put("tips", sectionsJson(tips.first, tips.second, entityIcon = { null }))
        }
    }

    private fun sectionsJson(sections: List<WikiHeroParser.Section>, lang: String, entityIcon: (String) -> String?): JSONObject = JSONObject().apply {
        put("lang", lang)
        put("sections", JSONArray().apply {
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
        })
    }

    private fun String.assetKey() = lowercase().replace(" ", "_")

    private fun heroIcon(name: String): String? = "heroes/${name.assetKey()}/minimap.webp".takeIf { File(assetsDatabase, it).exists() }

    private fun itemIcon(name: String): String? = "items/${name.assetKey()}/full.webp".takeIf { File(assetsDatabase, it).exists() }
}

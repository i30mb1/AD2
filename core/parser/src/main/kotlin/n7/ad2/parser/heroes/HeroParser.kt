@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.parser.heroes

import java.io.File
import n7.ad2.parser.LocaleHeroes
import n7.ad2.parser.assetsDatabase
import n7.ad2.parser.assetsDatabaseHeroes
import n7.ad2.parser.connectToWiki
import n7.ad2.parser.heroes.valve.ValveApi
import n7.ad2.parser.heroes.valve.ValveHeroParser
import n7.ad2.parser.heroes.valve.ValvePatchNotes
import n7.ad2.parser.heroes.wiki.WikiHeroParser
import n7.ad2.parser.heroes.wiki.WikiJson
import n7.ad2.parser.saveFile
import n7.ad2.parser.wikiText
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

// Configuration — override via env vars or JVM args:
//   TEST_MODE=false  or  -Dparser.testMode=false   → parse all heroes
//   TEST_LIMIT=10    or  -Dparser.testLimit=10      → change hero limit
//   HEROES=Pudge,Lion                                → parse only these heroes
//   PARSE_RESPONSES=true                             → also re-download voice responses from the wiki
private val TEST_MODE: Boolean = System.getenv("TEST_MODE")?.toBooleanStrictOrNull()
    ?: System.getProperty("parser.testMode")?.toBooleanStrictOrNull()
    ?: true
private val TEST_HERO_LIMIT: Int = System.getenv("TEST_LIMIT")?.toIntOrNull()
    ?: System.getProperty("parser.testLimit")?.toIntOrNull()
    ?: 3
private val ONLY_HEROES: Set<String> = System.getenv("HEROES")?.split(",")?.map { it.trim().lowercase() }?.filter { it.isNotEmpty() }?.toSet()
    ?: emptySet()
private val PARSE_RESPONSES: Boolean = System.getenv("PARSE_RESPONSES")?.toBooleanStrictOrNull() ?: false

/**
 * Asset path of an ability icon, relative to the assets root. Valve has no icons for some innate abilities,
 * the game shows the common innate icon for them.
 */
internal fun abilityImagePath(abilityKey: String, isInnate: Boolean): String {
    val path = "images/abilities/$abilityKey.webp"
    val hasOwnIcon = File(assetsDatabase, path).exists()
    return if (!hasOwnIcon && isInnate) "images/abilities/${ValveApi.INNATE_IMAGE_NAME}.webp" else path
}

// ===== WIKI PARSE FUNCTIONS =====

/** Parse the Trivia/Факты section: every list item until the next h2, without audio buttons. */
internal fun parseTrivia(doc: Document): List<String> {
    val triviaHeading = doc.select(
        "h2:has(span#Trivia), h2[id='Trivia'], h2:has(span#Факты), h2[id='Факты']",
    ).firstOrNull() ?: return emptyList()

    val result = mutableListOf<String>()
    var el: Element? = triviaHeading.nextElementSibling()
    while (el != null && el.tagName() != "h2") {
        val lists = if (el.tagName() == "ul") listOf(el) else el.select("ul")
        lists.forEach { list ->
            // a fact with a value the wiki failed to render reads as a broken sentence, it is skipped
            list.children().filter { it.tagName() == "li" && it.select(".error, .scribunto-error").isEmpty() }.forEach { item ->
                val copy = item.clone()
                copy.select(".ext-audiobutton, audio, sup.reference, .noprint, ul").remove()
                val text = copy.wikiText()
                if (text.isNotEmpty()) result.add(text)
            }
        }
        el = el.nextElementSibling()
    }
    return result
}

// ===== MAIN ENTRY POINT =====

fun main() {
    println("=== Dota 2 Hero Parser Started ===")

    val heroes = ValveApi.heroList()
    val existingFolders = File(assetsDatabaseHeroes).listFiles()?.filter { it.isDirectory }?.map { it.name }?.toSet() ?: emptySet()
    val (known, unknown) = heroes.partition { it.folderName in existingFolders }
    println("Found ${heroes.size} heroes in the datafeed, ${known.size} of them exist in the app")
    if (unknown.isNotEmpty()) println("➕ New in the app (pictures from the Valve CDN): ${unknown.joinToString { it.name }}")

    val heroesToProcess = when {
        ONLY_HEROES.isNotEmpty() -> heroes.filter { it.name.lowercase() in ONLY_HEROES || it.folderName in ONLY_HEROES }
        TEST_MODE -> known.take(TEST_HERO_LIMIT).also { println("⚠️  RUNNING IN TEST MODE - Only parsing first $TEST_HERO_LIMIT heroes") }
        else -> heroes
    }

    ValveApi.saveInnateImage()

    var successCount = 0
    val errors = mutableListOf<String>()
    heroesToProcess.forEachIndexed { index, hero ->
        println("Processing ${index + 1}/${heroesToProcess.size}: ${hero.name}")
        try {
            ValveApi.saveHeroImages(hero)
            val data = LocaleHeroes.entries.associateWith { locale -> ValveApi.heroData(hero.id, locale) }
            // icons first, the description refers to the files that really exist
            loadAbilityImages(data.getValue(LocaleHeroes.EN))
            data.forEach { (locale, heroData) -> loadHero(hero, locale, heroData, loadWiki(hero, locale)) }
            // a new hero has no voice lines yet, the wiki of a language may have none at all
            LocaleHeroes.entries
                .filter { locale -> PARSE_RESPONSES || !File(assetsDatabaseHeroes, "${hero.folderName}/${locale.folder}/responses.json").exists() }
                .forEach { locale ->
                    try {
                        loadResponses(hero, locale)
                    } catch (e: Exception) {
                        println("  WARNING: responses (${locale.folder}): ${e.message}")
                    }
                }
            successCount++
        } catch (e: Exception) {
            errors += "${hero.name}: ${e.message}"
            println("  ERROR ${hero.name}: ${e.message}")
        }
    }

    // the heroes screen lists only heroes whose page has data
    val listed = heroes.filter { File(assetsDatabaseHeroes, "${it.folderName}/${LocaleHeroes.EN.folder}/description.json").exists() }
    println(if (HeroList.save(listed)) "✓ heroes/list.json: ${listed.size} heroes" else "- heroes/list.json: no changes")

    println("\n=== Parsing Complete ===")
    println("Successfully parsed: $successCount heroes")
    println("Errors: ${errors.size}")
    errors.forEach { println("  - $it") }
}

// ===== PRIVATE ORCHESTRATION =====

private fun loadAbilityImages(data: JSONObject) {
    ValveHeroParser.abilityKeys(data).forEach { key ->
        try {
            ValveApi.saveAbilityImage(key)
        } catch (e: Exception) {
            println("  WARNING: ability image $key: ${e.message}")
        }
    }
}

private class WikiData(val pages: WikiJson.Pages, val trivia: List<String>)

/** Every part is best-effort: a missing subpage must not cost the hero its other data. */
private fun loadWiki(hero: ValveApi.HeroListItem, locale: LocaleHeroes): WikiData {
    fun <T> attempt(what: String, block: () -> T): T? = try {
        block()
    } catch (e: Exception) {
        println("  WARNING: wiki $what (${locale.folder}): ${e.message}")
        null
    }
    val page = attempt("page") { connectToWiki(locale.mainUrl.format(hero.name)) }
    val counters = attempt("counters") { WikiHeroParser.parseCounters(connectToWiki(locale.mainUrl.format("${hero.name}/${locale.countersPage}"))) }
    val guide = attempt("guide") { WikiHeroParser.parseGuide(connectToWiki(locale.mainUrl.format("${hero.name}/${locale.guidePage}"))) }
    return WikiData(
        pages = WikiJson.Pages(
            hero = page?.let { attempt("hero") { WikiHeroParser.parseHeroPage(it) } },
            counters = counters.orEmpty(),
            guide = guide,
        ),
        trivia = page?.let { attempt("trivia") { parseTrivia(it) } }.orEmpty(),
    )
}

/** [wiki] is the wiki of the same language, blocks it does not have are left out. */
private fun loadHero(hero: ValveApi.HeroListItem, locale: LocaleHeroes, data: JSONObject, wiki: WikiData) {
    val result = ValveHeroParser.parse(data, ::abilityImagePath)

    result["trivia"] = JSONArray().apply { addAll(wiki.trivia) }
    result["wiki"] = WikiJson.build(wiki.pages)
    @Suppress("UNCHECKED_CAST")
    (result["abilities"] as List<JSONObject>).forEach { ability ->
        WikiJson.abilityNotes((ability["name"] as String).lowercase(), wiki.pages)?.let { ability["wikiNotes"] = it }
    }

    val abilityNames = ValveHeroParser.abilityNamesById(data)
    result["changes"] = try {
        ValvePatchNotes.collect(hero.id, abilityNames, ValveHeroParser.values(data), locale)
    } catch (e: Exception) {
        println("  WARNING: patch notes (${locale.folder}): ${e.message}")
        JSONArray()
    }

    val path = "$assetsDatabaseHeroes/${hero.folderName}/${locale.folder}"
    val isSaved = saveFile(path, "description.json", result.toJSONString())
    println(if (isSaved) "  ✓ ${locale.folder}: saved" else "  - ${locale.folder}: no changes")
}

/** A page without a single line with a sound gives no file: the app tells there are no voice lines instead of empty categories. */
private fun loadResponses(hero: ValveApi.HeroListItem, locale: LocaleHeroes) {
    val categories = parseResponses(connectToWiki(locale.soundUrl.format(hero.name)))
    if (categories.isEmpty()) {
        println("  - responses ${locale.folder}: the wiki has no lines with a sound")
        return
    }
    File("$assetsDatabaseHeroes/${hero.folderName}/${locale.folder}/responses.json").writeText(categories.toString(), Charsets.UTF_8)
    println("  ✓ responses ${locale.folder} saved")
}

/**
 * Voice lines of a "Responses" ("Реплики") page by category. A line without a sound file ("File not found") is left
 * out, so is a category without lines. A line with an arcana or persona voice has a second sound, it becomes one more
 * line with the same text marked `isArcane`; the same text twice is not an arcana, heroes repeat their grunts.
 * Heroes and items a line is about keep their icon if the app has it.
 */
internal fun parseResponses(root: Document, assets: File = File(assetsDatabase)): JSONArray {
    val result = JSONArray()
    val children = root.getElementsByClass("mw-parser-output").firstOrNull()?.children() ?: return result
    var category: String? = null
    var lines = JSONArray()
    fun flush() {
        if (category != null && lines.isNotEmpty()) result.add(JSONObject().apply {
            put("category", category)
            put("responses", lines)
        })
        lines = JSONArray()
    }
    for (child in children) {
        when (child.tagName()) {
            "h2" -> {
                flush()
                category = (child.selectFirst(".mw-headline") ?: child).text().trim()
            }
            "ul" -> child.children().filter { it.tagName() == "li" }.forEach { item ->
                val sounds = item.select("audio a[href]").map { it.attr("href") }.filter { it.isNotEmpty() && !it.startsWith("/") }
                val sound = sounds.firstOrNull() ?: return@forEach
                val icons = item.select("span[typeof=mw:File] a[title]").mapNotNull { responseIcon(it.attr("title"), assets) }.distinct()
                val copy = item.clone().apply { select("span, ul").remove() }
                val title = copy.text().trim().takeIf { it.isNotEmpty() } ?: return@forEach
                fun line(url: String, isArcane: Boolean) = JSONObject().apply {
                    put("audio_url", url)
                    put("title", title)
                    if (icons.isNotEmpty()) put("icons", JSONArray().apply { addAll(icons) })
                    if (isArcane) put("isArcane", true)
                }
                lines.add(line(sound, isArcane = false))
                sounds.getOrNull(1)?.let { arcana -> lines.add(line(arcana, isArcane = true)) }
            }
        }
    }
    flush()
    return result
}

/** "Sven" → heroes/sven/minimap.webp, "Blink Dagger" → items/blink_dagger/full.webp, null when the app has neither. */
private fun responseIcon(title: String, assets: File): String? {
    val key = title.lowercase().replace(" ", "_")
    return listOf("heroes/$key/minimap.webp", "items/$key/full.webp").firstOrNull { File(assets, it).exists() }
}

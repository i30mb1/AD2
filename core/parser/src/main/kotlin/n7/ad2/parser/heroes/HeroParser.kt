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
                val text = copy.text()
                    .replace("Link ▶️", "")
                    .replace(Regex("""\s+"""), " ")
                    .trim()
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
    if (unknown.isNotEmpty()) println("⚠️  Not in the app yet (no folder in assets): ${unknown.joinToString { it.name }}")

    val heroesToProcess = when {
        ONLY_HEROES.isNotEmpty() -> known.filter { it.name.lowercase() in ONLY_HEROES || it.folderName in ONLY_HEROES }
        TEST_MODE -> known.take(TEST_HERO_LIMIT).also { println("⚠️  RUNNING IN TEST MODE - Only parsing first $TEST_HERO_LIMIT heroes") }
        else -> known
    }

    ValveApi.saveInnateImage()

    var successCount = 0
    val errors = mutableListOf<String>()
    heroesToProcess.forEachIndexed { index, hero ->
        println("Processing ${index + 1}/${heroesToProcess.size}: ${hero.name}")
        try {
            val data = LocaleHeroes.entries.associateWith { locale -> ValveApi.heroData(hero.id, locale) }
            // icons first, the description refers to the files that really exist
            loadAbilityImages(data.getValue(LocaleHeroes.EN))
            data.forEach { (locale, heroData) -> loadHero(hero, locale, heroData, loadWiki(hero, locale)) }
            if (PARSE_RESPONSES) LocaleHeroes.entries.forEach { locale -> loadResponses(hero, locale) }
            successCount++
        } catch (e: Exception) {
            errors += "${hero.name}: ${e.message}"
            println("  ERROR ${hero.name}: ${e.message}")
        }
    }

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
        ValvePatchNotes.collect(hero.id, abilityNames, locale)
    } catch (e: Exception) {
        println("  WARNING: patch notes (${locale.folder}): ${e.message}")
        JSONArray()
    }

    val path = "$assetsDatabaseHeroes/${hero.folderName}/${locale.folder}"
    val isSaved = saveFile(path, "description.json", result.toJSONString())
    println(if (isSaved) "  ✓ ${locale.folder}: saved" else "  - ${locale.folder}: no changes")
}

private fun loadResponses(hero: ValveApi.HeroListItem, locale: LocaleHeroes) {
    val root = connectToWiki(locale.soundUrl.format(hero.name))
    val allResponsesWithCategories = JSONArray()

    val children = root.getElementsByClass("mw-parser-output").firstOrNull()?.children() ?: return
    var category = JSONObject()
    var responses = JSONArray()
    var response: JSONObject

    for (child in children) {
        if (child.tag().toString() == "h2") {
            if (category.size != 0) allResponsesWithCategories.add(category)
            category = JSONObject()
            responses = JSONArray()
            category["category"] = if (child.children().size > 1) child.child(1).text().trim() else child.child(0).text().trim()
        }
        if (child.tag().toString() == "ul") {
            child.children().forEach node@{ node ->
                response = JSONObject()
                val audioUrl = node.getElementsByTag("a").getOrNull(0) ?: return@node
                val audioUrl2 = node.getElementsByTag("a").getOrNull(2)?.attr("href")?.toString()
                response["audio_url"] = audioUrl.attr("href").toString()
                response["title"] = node.let { innerNode ->
                    innerNode.getElementsByTag("span").forEach { span -> span.remove() }
                    innerNode.text()
                }
                if (node.getElementsByTag("img").size > 0) {
                    response["icons"] = JSONArray().apply {
                        node.getElementsByTag("span").forEach { span -> span.remove() }
                        node.getElementsByTag("a").forEach { image ->
                            val regex = Regex(" \\(.+?\\)")
                            val title = image.attr("title")
                            add(
                                if (regex.containsMatchIn(title)) {
                                    "items/" + title.replace(regex, "") + "/full.webp"
                                } else {
                                    "heroes/$title/minimap.webp"
                                },
                            )
                        }
                    }
                }
                val previousTitle = (responses.getOrNull(responses.size - 1) as? JSONObject)?.getOrDefault("title", "-")
                if (previousTitle == response["title"]) response["isArcane"] = true
                responses.add(response)

                if (audioUrl2 != null && !audioUrl2.startsWith("/")) {
                    val oldCopy = response
                    response = JSONObject()
                    if (oldCopy.containsKey("icons")) response["icons"] = oldCopy["icons"]
                    response["audioUrl"] = audioUrl2
                    response["title"] = oldCopy["title"]
                    response["isArcane"] = true
                    responses.add(response)
                }
            }
            category["responses"] = responses
        }
    }
    allResponsesWithCategories.add(category)

    File("$assetsDatabaseHeroes/${hero.folderName}/${locale.folder}/responses.json")
        .writeText(allResponsesWithCategories.toString(), Charsets.UTF_8)
    println("  ✓ responses ${locale.folder} saved")
}

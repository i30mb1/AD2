@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.parser.heroes

import java.io.File
import n7.ad2.parser.LocaleHeroes
import n7.ad2.parser.assetsDatabase
import n7.ad2.parser.assetsDatabaseHeroes
import n7.ad2.parser.assetsDatabaseSpells
import n7.ad2.parser.connectToWiki
import n7.ad2.parser.getTextFromNodeFormatted
import n7.ad2.parser.saveFile
import n7.ad2.parser.saveImage
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements

// Configuration — override via env vars or JVM args:
//   TEST_MODE=false  or  -Dparser.testMode=false   → parse all heroes
//   TEST_LIMIT=10    or  -Dparser.testLimit=10      → change hero limit
private val TEST_MODE: Boolean = System.getenv("TEST_MODE")?.toBooleanStrictOrNull()
    ?: System.getProperty("parser.testMode")?.toBooleanStrictOrNull()
    ?: true
private val TEST_HERO_LIMIT: Int = System.getenv("TEST_LIMIT")?.toIntOrNull()
    ?: System.getProperty("parser.testLimit")?.toIntOrNull()
    ?: 3

// ===== INTERNAL DATA CLASSES =====

internal data class BasicInfo(
    val mainAttribute: String,
    val attackType: String,
    val complexity: Int,
    val roles: List<String>,
)

internal data class MainAttributes(
    val attrStrength: Double,
    val attrStrengthInc: Double,
    val attrAgility: Double,
    val attrAgilityInc: Double,
    val attrIntelligence: Double,
    val attrIntelligenceInc: Double,
)

internal data class AbilityData(
    val name: String,
    val hotKey: String,
    val legacyKey: String,
    val audioUrl: String,
    val effects: List<String>,
    val description: String,
    val cooldown: String?,
    val mana: String?,
    val aghanimScepter: String?,
    val aghanimShard: String?,
    val itemBehaviour: List<String>,
    val story: String?,
    val notes: List<String>,
    val params: List<String>,
    val isInnate: Boolean,
    val spellImageUrl: String = "",
)

internal data class TalentRow(
    val talentLeft: String,
    val talentLvl: String,
    val talentRight: String,
)

internal data class BaseStats(
    val armor: Double,
    val damage: String,
    val movementSpeed: Int,
    val attackSpeed: String,
    val health: String,
    val mana: String,
)

// ===== INTERNAL PARSE FUNCTIONS =====

/** Parse the heroes list page. Returns heroes grouped by main attribute. */
internal fun parseHeroesList(document: Document): List<Hero> {
    val result = mutableListOf<Hero>()
    val attributeNames = listOf("Strength", "Agility", "Intelligence", "Universal")
    var currentAttribute = "Strength"

    // Primary: table rows alternate between attribute-header rows and hero-entry rows
    document.select("table tbody tr").forEach { row ->
        val attrImg = row.select("img[alt$=' attribute symbol']").firstOrNull()
        if (attrImg != null) {
            val altText = attrImg.attr("alt")
            currentAttribute = attributeNames.firstOrNull { altText.startsWith(it) } ?: currentAttribute
            return@forEach
        }
        row.select("a[href^='/wiki/'][title]").forEach { link ->
            val name = link.attr("title").trim()
            if (isValidHeroName(name)) {
                result.add(Hero(name, name.toFolderName(), currentAttribute, link.attr("href")))
            }
        }
    }

    // Fallback: if table approach yields nothing, try image-alt approach
    if (result.isEmpty()) {
        attributeNames.forEach { attrName ->
            val sectionImg = document.select("img[alt*='$attrName heroes']").firstOrNull() ?: return@forEach
            val container = generateSequence(sectionImg.parent()) { it.parent() }
                .take(5).firstOrNull { it.select("a[title]").size > 3 } ?: return@forEach
            container.select("a[href^='/wiki/'][title]").forEach { link ->
                val name = link.attr("title").trim()
                if (isValidHeroName(name)) {
                    result.add(Hero(name, name.toFolderName(), attrName, link.attr("href")))
                }
            }
        }
    }

    return result.distinctBy { it.name }
}

/** Parse hero basic info: attack type, complexity, roles from an individual hero page. */
internal fun parseBasicInfo(doc: Document, mainAttribute: String): BasicInfo {
    val attackType = when {
        doc.select("img[alt='Melee']").isNotEmpty() -> "Melee"
        doc.select("img[alt='Ranged']").isNotEmpty() -> "Ranged"
        else -> "Unknown"
    }

    val complexity = doc.select("img[alt='Hero Complexity']").size.coerceIn(1, 3)

    val rolesTd = doc.select("th:contains(Roles:), th:contains(Roles)").firstOrNull()
        ?.nextElementSibling()
    val roles = if (rolesTd != null) {
        rolesTd.select("img").map { it.attr("alt").trim() }
            .filter { it.isNotEmpty() && it.length < 30 && !it.contains("symbol", ignoreCase = true) }
    } else {
        doc.select("tr").firstOrNull { row -> row.text().startsWith("Roles:") }
            ?.select("img")?.map { it.attr("alt").trim() }
            ?.filter { it.isNotEmpty() && it.length < 30 } ?: emptyList()
    }

    return BasicInfo(mainAttribute, attackType, complexity, roles)
}

/** Parse hero background lore from infobox italic cell. */
internal fun parseDescription(doc: Document): String {
    val italicTd = doc.select("td[style*='font-style: italic'], td[style*='font-style:italic']")
        .firstOrNull()
    if (italicTd != null) {
        val text = italicTd.text().trim()
        if (text.length > 20) return text
    }
    // Fallback: original hardcoded index
    return doc.getElementsByTag("tbody").getOrNull(4)
        ?.getElementsByTag("td")?.text()?.trim() ?: ""
}

/** Parse hero history/lore from the display:table-cell italic element. */
internal fun parseHistory(doc: Document): String {
    return doc.select(
        "td[style*='display: table-cell'][style*='font-style: italic'], " +
            "td[style*='display:table-cell'][style*='font-style: italic']",
    ).firstOrNull()?.text()?.trim() ?: ""
}

/** Parse STR/AGI/INT base values and per-level increments. */
internal fun parseMainAttributes(doc: Document): MainAttributes {
    val container = doc.select("[style*='grid-template-columns: auto auto auto']").firstOrNull()
        ?: return MainAttributes(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

    // Regex approach: find three groups of "base + increment" in the container text
    val fullText = container.text().replace(",", ".")
    val pattern = Regex("""(\d+(?:\.\d+)?)\s*\+\s*(\d+(?:\.\d+)?)""")
    val matches = pattern.findAll(fullText).take(3).toList()

    if (matches.size >= 3) {
        return MainAttributes(
            attrStrength = matches[0].groupValues[1].toDouble(),
            attrStrengthInc = matches[0].groupValues[2].toDouble(),
            attrAgility = matches[1].groupValues[1].toDouble(),
            attrAgilityInc = matches[1].groupValues[2].toDouble(),
            attrIntelligence = matches[2].groupValues[1].toDouble(),
            attrIntelligenceInc = matches[2].groupValues[2].toDouble(),
        )
    }

    // Fallback: parse from div children
    val divs = container.getElementsByTag("div")
    val index = if (divs.size > 7) 7 else 4
    fun parseDiv(div: Element): Pair<Double, Double> {
        val text = (div.childNodes().filterIsInstance<TextNode>()
            .firstOrNull()?.text() ?: div.text()).replace(",", ".")
        val parts = text.trim().split(Regex("""[+\s]+"""))
        return (parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0) to
            (parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0)
    }
    if (index + 2 < divs.size) {
        val (str, strInc) = parseDiv(divs[index])
        val (agi, agiInc) = parseDiv(divs[index + 1])
        val (int, intInc) = parseDiv(divs[index + 2])
        return MainAttributes(str, strInc, agi, agiInc, int, intInc)
    }
    return MainAttributes(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
}

/** Parse hero base stats: armor, damage, move speed, attack speed, HP, mana. */
internal fun parseBaseStats(doc: Document): BaseStats {
    fun thLink(hrefPart: String) = doc.select("th:has(a[href*='$hrefPart'])").firstOrNull()

    val armor = thLink("Armor")?.nextElementSibling()
        ?.text()?.trim()?.toDoubleOrNull() ?: 0.0

    val damage = thLink("Attack_damage")?.nextElementSibling()?.text()?.trim() ?: ""

    // Movement speed: skip audio td, find the td whose text is a plain number
    val movementSpeed = thLink("Movement_speed")?.let { th ->
        generateSequence(th.nextElementSibling()) { it.nextElementSibling() }
            .firstOrNull { it.tagName() == "td" && it.text().matches(Regex("\\d+")) }
    }?.text()?.trim()?.toIntOrNull() ?: 0

    val health = thLink("Health")?.nextElementSibling()?.text()?.trim() ?: ""
    val mana = thLink("Mana")?.nextElementSibling()?.text()?.trim() ?: ""

    val attackSpeed = thLink("Attack_speed")?.let { th ->
        generateSequence(th.nextElementSibling()) { it.nextElementSibling() }
            .firstOrNull { it.tagName() == "td" && it.text().contains("BAT", ignoreCase = true) }
            ?.text()?.trim()
    } ?: ""

    return BaseStats(armor, damage, movementSpeed, attackSpeed, health, mana)
}

/** Parse all ability panels from a hero page. */
internal fun parseAbilities(doc: Document): List<AbilityData> {
    return doc.select("div.ability-background").mapNotNull { panel ->
        parseAbilityPanel(panel)
    }
}

/** Parse the Talents section from an individual hero page. */
internal fun parseTalents(doc: Document): List<TalentRow> {
    val talentsHeading = doc.select(
        "h2:has(span#Talents), h2[id='Talents'], h2:has(span:contains(Talents)), " +
            "h2:has(span#Таланты), h2[id='Таланты'], h2:has(span:contains(Таланты))",
    ).firstOrNull() ?: return emptyList()

    var el = talentsHeading.nextElementSibling()
    while (el != null && el.tagName() != "h2") {
        val rows = el.select("tr").filter { row ->
            row.children().size == 3 && row.child(1).tagName() == "th"
        }
        if (rows.isNotEmpty()) {
            return rows.map { row ->
                TalentRow(
                    talentLeft = row.child(0).text().trim(),
                    talentLvl = row.child(1).text().trim(),
                    talentRight = row.child(2).text().trim(),
                )
            }.filter { it.talentLeft.isNotEmpty() }
        }
        el = el.nextElementSibling()
    }
    return emptyList()
}

/** Parse the Trivia/Факты section from an individual hero page. */
internal fun parseTrivia(doc: Document): List<String> {
    val triviaHeading = doc.select(
        "h2:has(span#Trivia), h2[id='Trivia'], h2:has(span:contains(Trivia)), " +
            "h2:has(span#Факты), h2[id='Факты'], h2:has(span:contains(Факты))",
    ).firstOrNull() ?: return emptyList()

    var el = triviaHeading.nextElementSibling()
    while (el != null && el.tagName() != "h2") {
        val items = el.getElementsByTag("li")
        if (items.isNotEmpty()) {
            return items.map { it.text() }.filter { it.isNotEmpty() }
        }
        el = el.nextElementSibling()
    }
    return emptyList()
}

// ===== PRIVATE HELPERS =====

private fun isValidHeroName(name: String) =
    name.isNotEmpty() &&
        !name.contains("Category:") &&
        !name.contains("File:") &&
        !name.equals("Heroes", ignoreCase = true) &&
        name.length < 50

private fun String.toFolderName() = replace(" ", "_").lowercase()

/** Walk up parent chain to find the nearest ancestor matching [cssQuery] (Jsoup 1.11.x compat). */
private fun closestParent(el: Element, cssQuery: String): Element? {
    var current: Element? = el.parent()
    while (current != null) {
        if (current.`is`(cssQuery)) return current
        current = current.parent()
    }
    return null
}

private fun parseAbilityPanel(panel: Element): AbilityData? {
    val name = panel.select("span[style*='color:#fff'][style*='text-shadow']")
        .firstOrNull()?.text()?.trim() ?: return null
    if (name.isEmpty() || name.startsWith("<")) return null

    val spellImageUrl = panel.select("a.image").firstOrNull()?.attr("href") ?: ""
    val audioUrl = panel.select("source").attr("src").takeIf { it.isNotEmpty() } ?: ""

    val hotKey = panel.select("span.tooltip[title='Hotkey']").firstOrNull()
        ?.text()?.trim()?.takeIf { it.length == 1 } ?: ""
    val legacyKey = panel.select("span.tooltip[title='Legacy Key']").firstOrNull()
        ?.text()?.trim()?.takeIf { it.length == 1 } ?: ""

    val descBlock = panel.select("div.ability-description").firstOrNull()
    val effects = descBlock?.select("div[style*='width:32%'][style*='vertical-align:top']")
        ?.mapNotNull { col ->
            val label = col.select("b").firstOrNull()?.text()?.trim() ?: return@mapNotNull null
            val value = col.ownText().trim().ifEmpty { col.text().replace(label, "").trim() }
            if (label.isNotEmpty() && value.isNotEmpty()) "$label: $value" else null
        } ?: emptyList()

    val description = descBlock
        ?.select(
            "div[style*='font-size:95%'][style*='border-top'], " +
                "div[style*='vertical-align:top'][style*='padding:3px']",
        )
        ?.firstOrNull()?.text()?.trim() ?: ""

    val cooldown = panel.select("img[alt='Cooldown symbol']").firstOrNull()
        ?.let { closestParent(it, "div[style*='display:table-cell']") }
        ?.nextElementSibling()?.text()?.trim()

    val mana = panel.select("img[alt='Mana symbol']").firstOrNull()
        ?.let { closestParent(it, "div[style*='display:table-cell']") }
        ?.nextElementSibling()?.text()?.trim()

    // Aghanim upgrades: dark rgba(18, 26, 33) background divs (first = scepter, second = shard)
    val aghDivs = panel.select("div[style*='rgba(18, 26, 33']")
    val raw0 = aghDivs.getOrNull(0)?.text()?.trim()?.takeIf { "Aghanim" in it && it.length > 20 }
    val raw1 = aghDivs.getOrNull(1)?.text()?.trim()?.takeIf { "Aghanim" in it && it.length > 20 }
    val (aghanimScepter, aghanimShard) = when {
        aghDivs.size == 1 && raw0?.contains("Shard") == true -> null to raw0
        else -> raw0 to raw1
    }

    val itemBehaviour = panel.select("div[style*='margin-left: 50px']").mapNotNull { el ->
        val src = el.select("img").attr("src")
        val symbols = listOf(
            "Spell_immunity_block_partial_symbol.png",
            "Spell_block_partial_symbol.png",
            "Spell_immunity_block_symbol.png",
            "Disjointable_symbol.png",
            "Aghanim%27s_Scepter_symbol.png",
            "Breakable_symbol.png",
            "Breakable_partial_symbol.png",
        )
        val matched = symbols.firstOrNull { src.contains(it) } ?: return@mapNotNull null
        "(${matched.dropLast(4)})^" +
            el.select("[title]").firstOrNull()?.attr("title")?.dropLast(1) + "\n" +
            el.text().replace(". ", "\n")
    }

    val story = panel.select("div.ability-lore").firstOrNull()?.text()?.trim()?.takeIf { it.isNotEmpty() }

    val notes = panel.select("div[style*='word-wrap:break-word'] li")
        .map { it.text().trim() }
        .filter { it.isNotEmpty() }

    val params = try {
        val infoBlock = panel.select("div[style*='border:1px solid rgba(0, 0, 0, 0)']").firstOrNull()
        infoBlock?.select("div[style*='font-size:98%']")
            ?.map { getTextFromNodeFormatted(it) }
            ?.filter { it.isNotEmpty() } ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    val isInnate = panel.select("*:contains(Innate)").isNotEmpty() &&
        panel.text().contains("Innate", ignoreCase = true)

    return AbilityData(
        name = name,
        hotKey = hotKey,
        legacyKey = legacyKey,
        audioUrl = audioUrl,
        effects = effects,
        description = description,
        cooldown = cooldown,
        mana = mana,
        aghanimScepter = aghanimScepter,
        aghanimShard = aghanimShard,
        itemBehaviour = itemBehaviour,
        story = story,
        notes = notes,
        params = params,
        isInnate = isInnate,
        spellImageUrl = spellImageUrl,
    )
}

private fun List<AbilityData>.toJsonArray(): JSONArray {
    val array = JSONArray()
    forEach { ability ->
        @Suppress("UNCHECKED_CAST")
        val obj = JSONObject().apply {
            put("name", ability.name)
            put("hot_key", ability.hotKey)
            put("legacy_key", ability.legacyKey)
            put("audioUrl", ability.audioUrl)
            put("effects", JSONArray().also { a -> ability.effects.forEach { a.add(it) } })
            put("description", ability.description)
            put("cooldown", ability.cooldown)
            put("mana", ability.mana)
            if (ability.aghanimScepter != null) put("aghanim_scepter", ability.aghanimScepter)
            if (ability.aghanimShard != null) put("aghanim_shard", ability.aghanimShard)
            put("item_behaviour", JSONArray().also { a -> ability.itemBehaviour.forEach { a.add(it) } })
            put("story", ability.story)
            val notesList = ability.notes.takeIf { it.isNotEmpty() }
            put("notes", notesList?.let { notes -> JSONArray().also { a -> notes.forEach { a.add(it) } } })
            put("params", JSONArray().also { a -> ability.params.forEach { a.add(it) } })
            put("is_innate", ability.isInnate)
            put("aghanim", null) // legacy field
        }
        array.add(obj)
    }
    return array
}

// ===== MAIN ENTRY POINT =====

fun main() {
    println("=== Dota 2 Hero Parser Started ===")

    try {
        println("Step 1: Fetching heroes list...")
        val heroes = getHeroes()

        if (heroes.isEmpty()) {
            println("ERROR: No heroes found. Exiting...")
            return
        }

        println("Found ${heroes.size} heroes")

        println("Step 2: Creating heroes summary file...")
        try {
            createFileWithHeroes(heroes)
            println("Heroes summary file created successfully")
        } catch (e: Exception) {
            println("WARNING: Failed to create heroes summary file: ${e.message}")
        }

        println("Step 3: Parsing individual heroes...")
        if (TEST_MODE) println("⚠️  RUNNING IN TEST MODE - Only parsing first $TEST_HERO_LIMIT heroes")

        val heroesToProcess = if (TEST_MODE) heroes.take(TEST_HERO_LIMIT) else heroes
        var successCount = 0
        var errorCount = 0

        heroesToProcess.forEachIndexed { index, hero ->
            try {
                val progress = "${index + 1}/${heroesToProcess.size}"
                println("Processing $progress: ${hero.name}")

                val startTime = System.currentTimeMillis()
                loadHero(hero, LocaleHeroes.EN)
                loadHero(hero, LocaleHeroes.RU)
                val duration = System.currentTimeMillis() - startTime

                successCount++
                println("  ✓ Completed in ${duration}ms")

                if (index < heroesToProcess.size - 1) {
                    Thread.sleep(if (TEST_MODE) 200 else 500)
                }
            } catch (e: Exception) {
                errorCount++
                println("ERROR processing ${hero.name}: ${e.message}")
            }
        }

        println("\n=== Parsing Complete ===")
        println("Successfully parsed: $successCount heroes")
        println("Errors: $errorCount heroes")
    } catch (e: Exception) {
        println("CRITICAL ERROR: ${e.message}")
    }
}

// ===== PRIVATE ORCHESTRATION =====

private fun getHeroes(): List<Hero> {
    println("Connecting to ${LocaleHeroes.EN.heroesUrl}...")
    val document = connectToWiki(LocaleHeroes.EN.heroesUrl)

    val result = parseHeroesList(document)
    println("Successfully parsed ${result.size} unique heroes")
    result.take(5).forEach { hero -> println("  - ${hero.name} (${hero.mainAttribute})") }
    return result
}

private fun loadHero(hero: Hero, locale: LocaleHeroes) {
    val url = locale.mainUrl.format(hero.name)
    val root = connectToWiki(url)

    val result = JSONObject()

    try { loadHeroImage(root, hero) } catch (e: Exception) {
        println("  WARNING: Failed to load hero image: ${e.message}")
    }
    try { loadHeroMinimap(root, hero) } catch (e: Exception) {
        println("  WARNING: Failed to load hero minimap: ${e.message}")
    }
    try { result.loadBasicInfo(root, hero) } catch (e: Exception) {
        println("  WARNING: Failed to load basic info: ${e.message}")
    }
    try { result.loadDescription(root) } catch (e: Exception) {
        println("  WARNING: Failed to load description: ${e.message}")
    }
    try { result.loadHistory(root) } catch (e: Exception) {
        println("  WARNING: Failed to load history: ${e.message}")
    }
    try { result.loadAbilities(root) } catch (e: Exception) {
        println("  WARNING: Failed to load abilities: ${e.message}")
    }
    try { result.loadBaseStats(root) } catch (e: Exception) {
        println("  WARNING: Failed to load base stats: ${e.message}")
    }
    // Parse talents and trivia directly via doc.select() so they work regardless of
    // whether the h2 headings are direct children of mw-parser-output or nested deeper.
    try { result.addTalentsDirect(root) } catch (e: Exception) {
        println("  WARNING: Failed to load talents: ${e.message}")
    }
    try { result.addTriviasDirect(root) } catch (e: Exception) {
        println("  WARNING: Failed to load trivia: ${e.message}")
    }
    try { result.loadMainAttributes(root) } catch (e: Exception) {
        println("  WARNING: Failed to load main attributes: ${e.message}")
    }

    val path = "$assetsDatabase/heroes/${hero.folderName}/${locale.folder}"
    val isSaved = saveFile(path, "description.json", result.toJSONString())
    if (isSaved) println("  ✓ Hero data saved") else println("  - No changes (file up to date)")

    // Voice responses (best-effort)
    try {
        loadResponseLegacy(hero, locale)
        println("  ✓ Voice responses saved")
    } catch (e: Exception) {
        println("  WARNING: Voice responses: ${e.message}")
    }
}

private fun JSONObject.loadBasicInfo(root: Document, hero: Hero) {
    val info = parseBasicInfo(root, hero.mainAttribute)
    put("attackType", info.attackType)
    put("complexity", info.complexity)
    put("roles", JSONArray().apply { info.roles.forEach { add(it) } })
    put("mainAttribute", info.mainAttribute)
}

private fun JSONObject.loadDescription(root: Document) {
    put("description", parseDescription(root))
}

private fun JSONObject.loadHistory(root: Document) {
    put("history", parseHistory(root))
}

private fun JSONObject.loadMainAttributes(root: Document) {
    val attrs = parseMainAttributes(root)
    put(
        "mainAttributes",
        JSONObject().apply {
            put("attrStrength", attrs.attrStrength)
            put("attrStrengthInc", attrs.attrStrengthInc)
            put("attrAgility", attrs.attrAgility)
            put("attrAgilityInc", attrs.attrAgilityInc)
            put("attrIntelligence", attrs.attrIntelligence)
            put("attrIntelligenceInc", attrs.attrIntelligenceInc)
        },
    )
}

private fun JSONObject.loadAbilities(root: Document) {
    val abilities = parseAbilities(root)
    // Load spell images (best-effort)
    abilities.forEach { ability ->
        if (ability.spellImageUrl.isNotEmpty()) {
            try {
                val nameFormatted = ability.name.replace(" ", "_").lowercase()
                saveImage(ability.spellImageUrl, assetsDatabaseSpells, nameFormatted)
            } catch (e: Exception) {
                println("  WARNING: Spell image for ${ability.name}: ${e.message}")
            }
        }
    }
    put("abilities", abilities.toJsonArray())
}

private fun JSONObject.loadBaseStats(root: Document) {
    val stats = parseBaseStats(root)
    put(
        "baseStats",
        JSONObject().apply {
            put("armor", stats.armor)
            put("damage", stats.damage)
            put("movementSpeed", stats.movementSpeed)
            put("attackSpeed", stats.attackSpeed)
            put("health", stats.health)
            put("mana", stats.mana)
        },
    )
}

/** Parses talents directly from the document — works regardless of h2 nesting depth. */
private fun JSONObject.addTalentsDirect(doc: Document) {
    val talentRows = parseTalents(doc)
    if (talentRows.isEmpty()) return
    val talents = JSONArray().apply {
        talentRows.forEach { row ->
            add(
                JSONObject().apply {
                    put("talent_left", row.talentLeft)
                    put("talent_lvl", row.talentLvl)
                    put("talent_right", row.talentRight)
                },
            )
        }
    }
    put("talents", JSONObject().apply { put("hero_talents", talents) })
}

/** Parses trivia directly from the document — works regardless of h2 nesting depth. */
private fun JSONObject.addTriviasDirect(doc: Document) {
    val items = parseTrivia(doc)
    if (items.isEmpty()) return
    put("trivia", JSONArray().apply { items.forEach { add(it) } })
}

private fun createFileWithHeroes(heroes: List<Hero>) {
    val result = JSONArray()
    heroes.forEach { hero ->
        val heroObject = JSONObject()
        heroObject["name"] = hero.name
        heroObject["main_attribute"] = hero.mainAttribute
        heroObject["folder_name"] = hero.folderName
        heroObject["attack_type"] = hero.attackType
        heroObject["complexity"] = hero.complexity
        heroObject["roles"] = JSONArray().apply { hero.roles.forEach { add(it) } }
        result.add(heroObject)
    }
    saveFile(assetsDatabase, "heroes.json", result.toJSONString())
}

private fun loadResponseLegacy(hero: Hero, locale: LocaleHeroes) {
    val root = connectToWiki(locale.soundUrl.format(hero.name))
    val allResponsesWithCategories = JSONArray()

    JSONArray().apply {
        var count = 0
        val parserOutput = root.getElementsByClass("mw-parser-output")
        val children = parserOutput.firstOrNull()?.children()
            ?: return@loadResponseLegacy
        var category = JSONObject()
        var responses = JSONArray()
        var response: JSONObject

        for (child in children) {
            if (child.tag().toString() == "h2") {
                if (category.size != 0) allResponsesWithCategories.add(category)
                category = JSONObject()
                responses = JSONArray()

                count++
                if (child.children().size > 1) {
                    category["category"] = child.child(1).text().trim()
                } else {
                    category["category"] = child.child(0).text().trim()
                }
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
                            node.let { innerNode ->
                                innerNode.getElementsByTag("span").forEach { span -> span.remove() }
                                innerNode.getElementsByTag("a").forEach { image ->
                                    val regex = Regex(" \\(.+?\\)")
                                    var title = image.attr("title")
                                    val matches = regex.containsMatchIn(title)
                                    if (matches) {
                                        title = "items/" + title.replace(regex, "") + "/full.webp"
                                        add(title)
                                    } else {
                                        title = "heroes/$title/minimap.png"
                                        add(title)
                                    }
                                }
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
    }

    File("$assetsDatabaseHeroes/${hero.folderName}/${locale.folder}/responses.json")
        .writeText(allResponsesWithCategories.toString(), Charsets.UTF_8)
    println("response in ${locale.folder} for hero ${hero.name} saved (${allResponsesWithCategories.toString().length} bytes)")
}

private fun imgUrl(el: org.jsoup.nodes.Element) =
    el.attr("data-src").takeIf { it.isNotEmpty() }
        ?: el.attr("src").takeIf { it.isNotEmpty() } ?: ""

private fun Document.safeSelect(selector: String) = try {
    select(selector)
} catch (_: Exception) {
    org.jsoup.select.Elements()
}

private fun loadHeroMinimap(root: Document, hero: Hero) {
    // Safe name for use inside CSS attribute selectors (escape apostrophes)
    val safeName = hero.name.replace("'", "\\'")
    val minimapSelectors = listOf(
        "img[alt*='$safeName minimap icon']",
        "img[alt*='minimap']",
        ".minimap img",
        ".hero-minimap img",
    )
    val heroMinimapUrl = minimapSelectors
        .firstNotNullOfOrNull { sel -> root.safeSelect(sel).firstOrNull()?.let { imgUrl(it) }?.takeIf { it.isNotEmpty() } }
        ?: throw Exception("Could not find minimap image")
    saveImage(heroMinimapUrl, "$assetsDatabaseHeroes/${hero.folderName}", "minimap")
}

private fun loadHeroImage(root: Document, hero: Hero) {
    val safeName = hero.name.replace("'", "\\'")
    val heroImageUrl = sequenceOf(
        { root.safeSelect(".hero-portrait img").firstOrNull()?.let { imgUrl(it) } },
        { root.safeSelect(".portrait img").firstOrNull()?.let { imgUrl(it) } },
        { root.safeSelect(".hero-image img").firstOrNull()?.let { imgUrl(it) } },
        { root.safeSelect("img[alt*='$safeName']:not([alt*='minimap'])").firstOrNull()?.let { imgUrl(it) } },
        { root.safeSelect("img").getOrNull(2)?.let { imgUrl(it) } },
    ).mapNotNull { it() }.firstOrNull { it.isNotEmpty() }
        ?: throw Exception("Could not find hero image")
    saveImage(heroImageUrl, "$assetsDatabaseHeroes/${hero.folderName}", "full")
}


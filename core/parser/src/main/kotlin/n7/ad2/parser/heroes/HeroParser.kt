@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.parser.heroes

import n7.ad2.parser.LocaleHeroes
import n7.ad2.parser.assetsDatabase
import n7.ad2.parser.assetsDatabaseHeroes
import n7.ad2.parser.assetsDatabaseSpells
import n7.ad2.parser.getTextFromNodeFormatted
import n7.ad2.parser.saveFile
import n7.ad2.parser.saveImage
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.io.File

// Configuration
private const val TEST_MODE = true // Set to false for full parsing
private const val TEST_HERO_LIMIT = 3 // Only parse first N heroes in test mode

fun main() {
    println("=== Dota 2 Hero Parser Started ===")

    try {
        // Step 1: Get list of heroes
        println("Step 1: Fetching heroes list...")
        val heroes = getHeroes()

        if (heroes.isEmpty()) {
            println("ERROR: No heroes found. Exiting...")
            return
        }

        println("Found ${heroes.size} heroes")

        // Step 2: Create heroes summary file
        println("Step 2: Creating heroes summary file...")
        try {
            createFileWithHeroes(heroes)
            println("Heroes summary file created successfully")
        } catch (e: Exception) {
            println("WARNING: Failed to create heroes summary file: ${e.message}")
        }

        // Step 3: Parse individual heroes
        println("Step 3: Parsing individual heroes...")
        if (TEST_MODE) {
            println("⚠️  RUNNING IN TEST MODE - Only parsing first $TEST_HERO_LIMIT heroes")
        }

        val heroesToProcess = if (TEST_MODE) heroes.take(TEST_HERO_LIMIT) else heroes
        var successCount = 0
        var errorCount = 0

        heroesToProcess.forEachIndexed { index, hero ->
            try {
                val progress = "${index + 1}/${heroesToProcess.size}"
                println("Processing $progress: ${hero.name}")

                val startTime = System.currentTimeMillis()
                loadHero(hero, LocaleHeroes.EN)
                val duration = System.currentTimeMillis() - startTime

                successCount++
                println("  ✓ Completed in ${duration}ms")

                // Add small delay to be respectful to the server
                if (index < heroesToProcess.size - 1) {
                    Thread.sleep(if (TEST_MODE) 200 else 500)
                }
            } catch (e: Exception) {
                errorCount++
                println("ERROR processing ${hero.name}: ${e.message}")
                // Continue with next hero instead of crashing
            }
        }

        println("\n=== Parsing Complete ===")
        println("Successfully parsed: $successCount heroes")
        println("Errors: $errorCount heroes")
        println("Total: ${heroes.size} heroes")
    } catch (e: Exception) {
        println("CRITICAL ERROR: ${e.message}")
        // TODO replace with logger
        // e.printStackTrace()
    }
}

private fun loadResponseLegacy(hero: Hero, locale: LocaleHeroes) {
    val root = Jsoup.connect(locale.soundUrl.format(hero.name)).postDataCharset("UTF-8").get()
    val allResponsesWithCategories = JSONArray()

    JSONArray().apply {
        var count = 0
        val children = root.getElementsByAttributeValue("class", "mw-parser-output")[0].children()
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
//                        if(child.child(0).children().size == 0) continue // реплики без URL
//                        if(child.children().size >1) // cекция без реплик

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

    File("$assetsDatabaseHeroes/${hero.name}/${locale.folder}/responses.json").writeText(allResponsesWithCategories.toString())
    println("response in ${locale.folder} for hero ${hero.name} saved (${allResponsesWithCategories.toString().length} bytes)")
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
        heroObject["roles"] = JSONArray().apply {
            hero.roles.forEach { add(it) }
        }
        result.add(heroObject)
    }
    saveFile(assetsDatabase, "heroes.json", result.toJSONString())
}

private fun getHeroes(): List<Hero> {
    println("Connecting to ${LocaleHeroes.EN.heroesUrl}...")

    val document = try {
        Jsoup.connect(LocaleHeroes.EN.heroesUrl)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .timeout(10000) // 10 second timeout
            .postDataCharset("UTF-8")
            .get()
    } catch (e: Exception) {
        println("ERROR: Failed to connect to heroes page: ${e.message}")
        throw e
    }

    val result = mutableListOf<Hero>()

    // Method 1: Try modern parsing with attribute sections
    try {
        println("Trying modern attribute-based parsing...")
        val attributeSections = mapOf(
            "Strength" to "strength",
            "Agility" to "agility",
            "Intelligence" to "intelligence",
            "Universal" to "universal",
        )

        attributeSections.forEach { (attributeName, _) ->
            try {
                // Multiple selectors to find heroes in each section
                val possibleSelectors = listOf(
                    "img[alt*='$attributeName heroes']",
                    "*:contains($attributeName) + * a[title]",
                    "h2:contains($attributeName) ~ * a[title]",
                )

                var heroElements: List<Element> = emptyList()
                for (selector in possibleSelectors) {
                    val elements = document.select(selector)
                    if (elements.isNotEmpty()) {
                        heroElements = elements.first()?.parent()?.parent()?.select("a[title]") ?: emptyList()
                        if (heroElements.isNotEmpty()) break
                    }
                }

                var heroCount = 0
                heroElements.forEach { element ->
                    val heroName = element.attr("title").trim()
                    if (heroName.isNotEmpty() &&
                        !heroName.contains("Category:", ignoreCase = true) &&
                        !heroName.contains("File:", ignoreCase = true) &&
                        !heroName.contains("Heroes", ignoreCase = true) &&
                        heroName.length < 50
                    ) {
                        val href = element.attr("href")
                        val folderName = heroName.replace(" ", "_").lowercase()
                        result.add(Hero(heroName, folderName, attributeName, href))
                        heroCount++
                    }
                }
                println("Found $heroCount $attributeName heroes")
            } catch (e: Exception) {
                println("WARNING: Error parsing $attributeName heroes: ${e.message}")
            }
        }
    } catch (e: Exception) {
        println("WARNING: Modern parsing failed: ${e.message}")
    }

    // Method 2: Fallback to old parsing if new method fails
    if (result.isEmpty()) {
        println("Falling back to legacy parsing method...")
        try {
            val heroesTable = document.getElementsByAttributeValue("style", "text-align:center").firstOrNull()
                ?: document.select(".herolist, .hero-grid").firstOrNull()
                ?: throw Exception("Could not find heroes table")

            val elements = heroesTable.getElementsByAttributeValue("style", "width:150px; height:84px; display:inline-block; overflow:hidden; margin:1px")

            if (elements.isEmpty()) {
                // Try alternative selector
                elements.addAll(
                    document.select("a[title]:has(img)").filter {
                        it.attr("title").isNotEmpty() && !it.attr("title").contains("File:")
                    },
                )
            }

            var heroMainAttribute = "Strength"
            for (element in elements) {
                try {
                    val heroName = element.getElementsByTag("a").firstOrNull()?.attr("title")
                        ?: element.attr("title")

                    if (heroName.isNotEmpty()) {
                        // Determine attribute based on hero name (legacy method)
                        when (heroName) {
                            "Anti-Mage" -> heroMainAttribute = "Agility"
                            "Ancient Apparition" -> heroMainAttribute = "Intelligence"
                        }

                        val href = element.getElementsByTag("a").firstOrNull()?.attr("href")
                            ?: element.attr("href")
                        val folderName = heroName.replace(" ", "_").lowercase()
                        result.add(Hero(heroName, folderName, heroMainAttribute, href))
                    }
                } catch (e: Exception) {
                    println("WARNING: Error parsing hero element: ${e.message}")
                }
            }
        } catch (e: Exception) {
            println("ERROR: Legacy parsing also failed: ${e.message}")
            throw Exception("All parsing methods failed", e)
        }
    }

    val uniqueResult = result.distinctBy { it.name }
    println("Successfully parsed ${uniqueResult.size} unique heroes")

    // Debug: print first few heroes
    uniqueResult.take(5).forEach { hero ->
        println("  - ${hero.name} (${hero.mainAttribute})")
    }

    return uniqueResult
}

private fun loadHero(hero: Hero, locale: LocaleHeroes) {
    val url = locale.mainUrl.format(hero.name)

    val root = try {
        Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .timeout(10000)
            .get()
    } catch (e: Exception) {
        throw Exception("Failed to load hero page for ${hero.name}: ${e.message}", e)
    }

    val result = JSONObject()

    // Load hero images with error handling
    try {
        loadHeroImage(root, hero)
    } catch (e: Exception) {
        println("  WARNING: Failed to load hero image: ${e.message}")
    }

    try {
        loadHeroMinimap(root, hero)
    } catch (e: Exception) {
        println("  WARNING: Failed to load hero minimap: ${e.message}")
    }

    // Load various sections with individual error handling
    try {
        result.loadBasicInfo(root, hero)
    } catch (e: Exception) {
        println("  WARNING: Failed to load basic info: ${e.message}")
    }

    try {
        result.loadDescription(root)
    } catch (e: Exception) {
        println("  WARNING: Failed to load description: ${e.message}")
    }

    try {
        result.loadHistory(root)
    } catch (e: Exception) {
        println("  WARNING: Failed to load history: ${e.message}")
    }

    try {
        result.loadAbilities(root)
    } catch (e: Exception) {
        println("  WARNING: Failed to load abilities: ${e.message}")
    }

    try {
        result.loadBaseStats(root)
    } catch (e: Exception) {
        println("  WARNING: Failed to load base stats: ${e.message}")
    }

    try {
        loadSections(root) { sectionAndData: SectionAndData ->
            try {
                when (sectionAndData.name) {
                    "Talents", "Таланты" -> result.addTalents(sectionAndData)
                    "Trivia", "Факты" -> result.addTrivia(sectionAndData)
                    "Facets", "Фасеты" -> result.addFacets(sectionAndData)
                }
            } catch (e: Exception) {
                println("  WARNING: Failed to load section ${sectionAndData.name}: ${e.message}")
            }
        }
    } catch (e: Exception) {
        println("  WARNING: Failed to load sections: ${e.message}")
    }

    try {
        result.loadMainAttributes(root)
    } catch (e: Exception) {
        println("  WARNING: Failed to load main attributes: ${e.message}")
    }

    // Save the result
    try {
        val path = "$assetsDatabase/heroes/${hero.folderName}/${locale.folder}"
        val isSaved = saveFile(path, "description.json", result.toJSONString())
        if (isSaved) {
            println("  ✓ Hero data saved")
        } else {
            println("  - No changes (file up to date)")
        }
    } catch (e: Exception) {
        throw Exception("Failed to save hero data for ${hero.name}: ${e.message}", e)
    }
}

private fun loadHeroMinimap(root: Document, hero: Hero) {
    val minimapSelectors = listOf(
        "img[alt*='${hero.name} minimap icon']",
        "img[alt*='minimap']",
        ".minimap img",
        ".hero-minimap img",
    )

    var heroMinimapUrl = ""
    for (selector in minimapSelectors) {
        val element = root.select(selector).firstOrNull()
        if (element != null) {
            heroMinimapUrl = element.attr("data-src").takeIf { it.isNotEmpty() }
                ?: element.attr("src").takeIf { it.isNotEmpty() }
                ?: ""
            if (heroMinimapUrl.isNotEmpty()) break
        }
    }

    if (heroMinimapUrl.isNotEmpty()) {
        saveImage(heroMinimapUrl, "$assetsDatabaseHeroes/${hero.name}", "minimap")
    } else {
        throw Exception("Could not find minimap image")
    }
}

private fun loadHeroImage(root: Document, hero: Hero) {
    // Try multiple methods to find the hero's main image
    val imageSelectors = listOf(
        ".hero-portrait img",
        ".portrait img",
        ".hero-image img",
        "img[alt*='${hero.name}']:not([alt*='minimap'])",
        "img",
    )

    var heroImageUrl = ""
    for (selector in imageSelectors) {
        val elements = root.select(selector)
        for ((index, element) in elements.withIndex()) {
            val url = element.attr("data-src").takeIf { it.isNotEmpty() }
                ?: element.attr("src").takeIf { it.isNotEmpty() }
                ?: ""

            // For generic img selector, try index 2 (legacy behavior) first
            if (selector == "img" && index == 2 && url.isNotEmpty()) {
                heroImageUrl = url
                break
            } else if (selector != "img" && url.isNotEmpty()) {
                heroImageUrl = url
                break
            }
        }
        if (heroImageUrl.isNotEmpty()) break
    }

    if (heroImageUrl.isNotEmpty()) {
        saveImage(heroImageUrl, "$assetsDatabaseHeroes/${hero.name}", "full")
    } else {
        throw Exception("Could not find hero image")
    }
}

private fun JSONObject.addTrivia(sectionAndData: SectionAndData) {
    val trivias = sectionAndData.data[1].getElementsByTag("li")
    JSONArray().apply {
        for (trivia in trivias) add(trivia.text())
        put("trivia", this)
    }
}

private fun JSONObject.addTalents(sectionAndData: SectionAndData) {
    val talentBlock = sectionAndData.data[1].getElementsByAttributeValue("style", "display:flex; flex-wrap:wrap; align-items:flex-start;")[0]
    val talentLines = talentBlock.getElementsByTag("tr")
    val talents = JSONArray().apply {
        var talentLvl = 25
        for (talentLine in talentLines) {
            if (talentLine.children().size == 1) continue
            JSONObject().apply {
                put("talent_left", talentLine.child(0).text())
                put("talent_lvl", talentLvl.toString())
                put("talent_right", talentLine.child(2).text())
                add(this)
            }
            talentLvl -= 5
        }
    }

    val notes = JSONArray().apply {
        val talentTips = talentBlock.getElementsByTag("li")
        for (talentTip in talentTips) add(talentTip.text())
    }
    val result = JSONObject().apply {
        put("hero_talents", talents)
        if (notes.size > 0) put("notes", notes)
    }
    put("talents", result)
}

private fun JSONObject.loadMainAttributes(root: Document) {
    val mainAttributes = root.getElementsByAttributeValue("style", "width:100%; padding:4px 0; display:grid; grid-template-columns: auto auto auto; color:white; text-align:center;")[0]
    val mainAttributesElements = mainAttributes.getElementsByTag("div")

    var index = 4
    if (mainAttributesElements.size > 7) index = 7
    val attrStrength = (mainAttributesElements[index].childNode(0) as TextNode).text().split(" ").first().toDouble()
    val attrStrengthInc = (mainAttributesElements[index].childNode(0) as TextNode).text().split(" ").last().replace(",", ".").toDouble()
    val attrAgility = (mainAttributesElements[index + 1].childNode(0) as TextNode).text().split(" ").first().toDouble()
    val attrAgilityInc = (mainAttributesElements[index + 1].childNode(0) as TextNode).text().split(" ").last().replace(",", ".").toDouble()
    val attrIntelligence = (mainAttributesElements[index + 2].childNode(0) as TextNode).text().split(" ").first().toDouble()
    val attrIntelligenceInc = (mainAttributesElements[index + 2].childNode(0) as TextNode).text().split(" ").last().replace(",", ".").toDouble()

    val attrs = JSONObject().apply {
        put("attrStrength", attrStrength)
        put("attrStrengthInc", attrStrengthInc)
        put("attrAgility", attrAgility)
        put("attrAgilityInc", attrAgilityInc)
        put("attrIntelligence", attrIntelligence)
        put("attrIntelligenceInc", attrIntelligenceInc)
    }
    put("mainAttributes", attrs)
}

private data class SectionAndData(val name: String, val data: Elements)

private fun loadSections(root: Document, callback: (SectionAndData) -> Unit) {
    val sections: Elements = root.getElementsByAttributeValue("class", "mw-parser-output")[0].children()
    var lastName = ""
    var data = Elements()
    for (section in sections) {
        if (section.tag().toString() == "h2") {
            val name = section.child(0).id()
            if (lastName != name) {
                callback(SectionAndData(lastName, data))
                data = Elements()
            }
            lastName = name
        }
        data.add(section)
    }
}

private fun JSONObject.loadAbilities(root: Document) {
    val spells = root.getElementsByAttributeValue("style", "display:flex; flex-wrap:wrap; align-items:flex-start;")
    val abilitiesArray = JSONArray()
    for (spell in spells) {
        val abilityObject = JSONObject()
        val section = spell.getElementsByTag("div").getOrNull(3) ?: continue
        val name = section.childNode(0).toString().trim()
        if (name.startsWith("<div ")) continue
        abilityObject["name"] = name

        loadSpellImage(spell, name)

        val audioUrl = spell.getElementsByTag("source").attr("src")
        abilityObject["audioUrl"] = audioUrl

        val hotKey = spell.getElementsByAttributeValue("class", "tooltip").getOrNull(0)?.text()?.takeIf { it.length == 1 } ?: ""
        abilityObject["hot_key"] = hotKey

        val legacyKey = spell.getElementsByAttributeValue("class", "tooltip").getOrNull(1)?.text()?.takeIf { it.length == 1 } ?: ""
        abilityObject["legacy_key"] = legacyKey

        val effects = spell.getElementsByAttributeValue("style", "display:inline-block; width:32%; vertical-align:top;")
        val jsonEffects = JSONArray()
        for (element in effects) {
            if (element.childNodeSize() < 3) continue
            val firstPart = (element.childNode(0) as? Element)?.text() ?: (element.childNode(0) as? TextNode)?.text() ?: continue
            val secondPart = (element.childNode(2) as? Element)?.text() ?: (element.childNode(2) as? TextNode)?.text() ?: continue
            val effect = "$firstPart: $secondPart"
            jsonEffects.add(effect)
        }
        abilityObject["effects"] = jsonEffects

        val description = spell.getElementsByTag("div")[12].text()
        abilityObject["description"] = description

        val cooldown = spell.getElementsByAttributeValue("style", "display:table-cell; margin:4px 0px 0px 0px; width:240px;").getOrNull(0)
        abilityObject["cooldown"] = cooldown?.text()

        val mana = spell.getElementsByAttributeValue("style", "display:table-cell; margin:4px 0px 0px 0px; max-width:100%; width:240px;").getOrNull(0)
        abilityObject["mana"] = mana?.text()

        // Parse Aghanim's Scepter upgrades
        val scepterUpgrade = spell.select("*:contains(Aghanim's Scepter)")
            .filter { it.text().contains("Aghanim's Scepter") && it.text().length > 20 }
            .firstOrNull()?.text()
        if (!scepterUpgrade.isNullOrEmpty()) {
            abilityObject["aghanim_scepter"] = scepterUpgrade
        }

        // Parse Aghanim's Shard upgrades
        val shardUpgrade = spell.select("*:contains(Aghanim's Shard)")
            .filter { it.text().contains("Aghanim's Shard") && it.text().length > 20 }
            .firstOrNull()?.text()
        if (!shardUpgrade.isNullOrEmpty()) {
            abilityObject["aghanim_shard"] = shardUpgrade
        }

        val itemBehaviour = spell.getElementsByAttributeValue("style", "margin-left: 50px;")
        JSONArray().apply {
            itemBehaviour.forEach {
                val alt = it.getElementsByTag("img").attr("src")
                ifContainAdd(alt, "Spell_immunity_block_partial_symbol.png", it)
                ifContainAdd(alt, "Spell_block_partial_symbol.png", it)
                ifContainAdd(alt, "Spell_immunity_block_symbol.png", it)
                ifContainAdd(alt, "Disjointable_symbol.png", it)
                ifContainAdd(alt, "Aghanim%27s_Scepter_symbol.png", it)
                ifContainAdd(alt, "Breakable_symbol.png", it)
                ifContainAdd(alt, "Breakable_partial_symbol.png", it)
            }

            abilityObject["item_behaviour"] = this
        }

        val story = spell.getElementsByAttributeValue("style", "display:inline-block; width:450px; margin-top:5px; padding:2px 10px 5px; text-align:center;").getOrNull(0)
        abilityObject["story"] = story?.text()

        val notesBlock = spell.getElementsByAttributeValue("style", "flex:1 1 450px; word-wrap:break-word;").getOrNull(0)
        val notesArray = JSONArray()
        notesBlock?.children()?.forEach notesForEach@{ element ->
            if (element.tagName() != "ul") return@notesForEach
            val result = getTextFromNodeFormatted(element)
            if (result.isNotEmpty()) notesArray.add(result)
        }

        abilityObject["notes"] = notesArray.ifEmpty { null }

        val infoBlock = spell.getElementsByAttributeValue("style", "display:inline-block; vertical-align:top; padding:3px 5px; border:1px solid rgba(0, 0, 0, 0);").getOrNull(0)
        val jsonParams = JSONArray()
        if (infoBlock != null) {
            for (block in infoBlock.children()) {
                val style = block.attributes()["style"]
                when (style) {
                    "font-size:98%;" -> {
                        val result = getTextFromNodeFormatted(block)
                        jsonParams.add(result)
                    }
                }
            }
        }
        abilityObject["params"] = jsonParams

        // Check if this is an Innate ability
        val isInnate = spell.select("*:contains(Innate)").isNotEmpty() ||
            name.contains("Innate", ignoreCase = true)
        abilityObject["is_innate"] = isInnate

        // Legacy field for backwards compatibility
        abilityObject["aghanim"] = null

        abilitiesArray.add(abilityObject)
    }

    put("abilities", abilitiesArray)
}

private fun loadSpellImage(element: Element, name: String) {
    val url = element.getElementsByAttributeValue("class", "image")[0].attr("href")
    val nameFormatted = name.replace(" ", "_").lowercase()
    saveImage(url, assetsDatabaseSpells, nameFormatted)
}

private fun JSONArray.ifContainAdd(alt: String, spellImmunityBlockPartial: String, it: Element) {
    if (alt.contains(spellImmunityBlockPartial)) {
        add("(${spellImmunityBlockPartial.dropLast(4)})^" + it.getElementsByAttribute("title")[0].attr("title").dropLast(1) + "\n" + it.text().replace(". ", "\n"))
    }
}

private fun JSONObject.loadHistory(root: Document) {
    var history = root.getElementsByAttributeValue("style", "display: table-cell; font-style: italic;").getOrNull(0)?.text()
    if (history == null) history = root.getElementsByAttributeValue("style", "display:table-cell; font-style: italic;").getOrNull(0)?.text()
    put("history", history)
}

private fun JSONObject.loadBasicInfo(root: Document, hero: Hero) {
    // Parse attack type (Melee/Ranged)
    val attackType = try {
        root.select("tr:contains(Attack Type) td").text().trim()
    } catch (e: Exception) {
        "Unknown"
    }

    // Parse complexity rating (1-3 stars)
    val complexity = try {
        root.select("img[alt*='Complexity']").size
    } catch (e: Exception) {
        1
    }

    // Parse roles
    val roles = try {
        root.select("tr:contains(Role) td a").map { it.text().trim() }
    } catch (e: Exception) {
        emptyList<String>()
    }

    put("attackType", attackType)
    put("complexity", complexity)
    put("roles", JSONArray().apply { roles.forEach { add(it) } })
    put("mainAttribute", hero.mainAttribute)
}

private fun JSONObject.loadBaseStats(root: Document) {
    val baseStats = JSONObject()

    try {
        // Parse armor, damage, movement speed, etc.
        val armorText = root.select("tr:contains(Armor) td").text()
        val armor = armorText.split(" ").firstOrNull()?.toDoubleOrNull() ?: 0.0
        baseStats["armor"] = armor

        val damageText = root.select("tr:contains(Attack damage) td").text()
        baseStats["damage"] = damageText

        val movementSpeed = root.select("tr:contains(Movement speed) td").text()
            .split(" ").firstOrNull()?.toIntOrNull() ?: 0
        baseStats["movementSpeed"] = movementSpeed

        val attackSpeed = root.select("tr:contains(Base attack time) td").text()
        baseStats["attackSpeed"] = attackSpeed

        val health = root.select("tr:contains(Health) td").text()
        baseStats["health"] = health

        val mana = root.select("tr:contains(Mana) td").text()
        baseStats["mana"] = mana
    } catch (e: Exception) {
        println("Error parsing base stats for hero: ${e.message}")
    }

    put("baseStats", baseStats)
}

private fun JSONObject.addFacets(sectionAndData: SectionAndData) {
    val facetsArray = JSONArray()

    try {
        // Parse facets if they exist - newer game mechanic
        val facetElements = sectionAndData.data.select("div.facet, .facet-container")
        facetElements.forEach { facetElement ->
            val facetObj = JSONObject()
            val facetName = facetElement.select("h3, .facet-name").text()
            val facetDescription = facetElement.select("p, .facet-description").text()

            if (facetName.isNotEmpty()) {
                facetObj["name"] = facetName
                facetObj["description"] = facetDescription
                facetsArray.add(facetObj)
            }
        }
    } catch (e: Exception) {
        println("Error parsing facets: ${e.message}")
    }

    if (facetsArray.isNotEmpty()) {
        put("facets", facetsArray)
    }
}

private fun JSONObject.loadDescription(root: Document) {
    val description = root.getElementsByTag("tbody")[4].getElementsByTag("td").text()
    put("description", description)
}

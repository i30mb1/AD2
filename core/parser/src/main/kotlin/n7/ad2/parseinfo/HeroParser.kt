@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.parseinfo

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.io.File

fun main() {
    val heroes = getHeroes()
    createFileWithHeroes(heroes)
    for (hero in heroes) {
        loadHero(hero, LocaleHeroes.RU)
        loadHero(hero, LocaleHeroes.EN)
    }
}

private fun loadResponse(hero: Hero, locale: LocaleHeroes) {
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
        result.add(heroObject)
    }
    File("$assetsDatabase/heroes.json").writeText(result.toJSONString())
}

private fun getHeroes(): List<Hero> {
    val document = Jsoup.connect(LocaleHeroes.EN.heroesUrl).postDataCharset("UTF-8").get()
    val heroesTable = document.getElementsByAttributeValue("style", "text-align:center")[0]
    val elements = heroesTable.getElementsByAttributeValue("style", "width:150px; height:84px; display:inline-block; overflow:hidden; margin:1px")
    var heroMainAttribute = "Strength"
    val result = mutableListOf<Hero>()
    for (element in elements) {
        val heroName = element.getElementsByTag("a")[0].attr("title")
        if (heroName == "Anti-Mage") heroMainAttribute = "Agility"
        if (heroName == "Ancient Apparition") heroMainAttribute = "Intelligence"
        val href = element.getElementsByTag("a")[0].attr("href")
        val folderName = heroName.replace(" ", "_").lowercase()
        result.add(Hero(heroName, folderName, heroMainAttribute, href))
    }
    return result
}

private fun loadHero(hero: Hero, locale: LocaleHeroes) {
    val root = Jsoup.connect(locale.mainUrl.format(hero.name)).get()
    loadHeroImage(root, hero)
    loadHeroMinimap(root, hero)
    val result = JSONObject()
    result.loadDescription(root)
    result.loadHistory(root)
    result.loadAbilities(root)
    loadSections(root) { sectionAndData: SectionAndData ->
        when (sectionAndData.name) {
            "Talents", "Таланты" -> result.addTalents(sectionAndData)
            "Trivia", "Факты" -> result.addTrivia(sectionAndData)
        }
    }
    result.loadMainAttributes(root)

    val path = "$assetsDatabase/heroes/${hero.folderName}/${locale.folder}"
    val isSaved = saveFile(path, "description.json", result.toJSONString())
    if (isSaved) println("hero ${hero.name}-${locale.folder} info saved")
}

private fun loadHeroMinimap(root: Document, hero: Hero) {
    val heroMinimap = root.getElementsByAttributeValue("alt", "${hero.name} minimap icon.png")[0].attr("data-src")
    saveImage(heroMinimap, "$assetsDatabaseHeroes/${hero.name}", "minimap")
}

private fun loadHeroImage(root: Document, hero: Hero) {
    val heroImage = root.getElementsByTag("img")[2].attr("data-src")
    saveImage(heroImage, "$assetsDatabaseHeroes/${hero.name}", "full")
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

data class SectionAndData(val name: String, val data: Elements)

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
        if (cooldown?.getElementsByAttribute("href")?.getOrNull(0)?.attr("href").equals("/Aghanim%27s_Scepter")) {
            abilityObject["cooldown"] = cooldown?.text()
        } else {
            abilityObject["cooldown"] = cooldown?.text()
            // todo add AGHANIM SHARD ебучий
        }

        val mana = spell.getElementsByAttributeValue("style", "display:table-cell; margin:4px 0px 0px 0px; max-width:100%; width:240px;").getOrNull(0)
        if (mana?.getElementsByAttribute("href")?.getOrNull(0)?.attr("href").equals("/Aghanim%27s_Scepter")) {
            abilityObject["mana"] = mana?.text()?.replace("(", "(TagAghanim")
        } else {
            abilityObject["mana"] = mana?.text()?.replace("(", "(TagTalent")
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
        if (infoBlock != null) for (block in infoBlock.children()) {
            val style = block.attributes()["style"]
            val params = listOf<String>()
            when (style) {
                "font-size:98%;" -> {
                    val result = getTextFromNodeFormatted(block)
                    jsonParams.add(result)
                }
            }


//            for (element in param.children()) {
//                val elementText = when (element.tagName()) {
//                    "span" -> {
//                        val result = StringBuilder()
//                        for (child in element.children()) {
//                            if (child.tagName() == "a") result.append("<spannnn>")
//                            else result.append(child.text())
//                        }
//                        result.toString()
//                    }
//                    else -> " " + element.text()
//                }
//                result.append(elementText)
//            }
        }
        abilityObject["params"] = jsonParams
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

private fun JSONObject.loadDescription(root: Document) {
    val description = root.getElementsByTag("tbody")[4].getElementsByTag("td").text()
    put("description", description)
}
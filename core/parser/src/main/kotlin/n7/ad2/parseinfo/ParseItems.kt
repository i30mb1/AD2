@file:Suppress("unused")

package n7.ad2.parseinfo

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.io.File

private val getItemsUseCase = GetItemsUseCase()

fun main() {
//    val itemsEnglish = getItemsUseCase(LOCALE.EN)
//    writeItemsInFile(itemsEnglish)
    loadItemsOneByOne(LOCALE.EN, true)
    loadItemsOneByOne(LOCALE.RU, false)
}

private fun writeItemsInFile(items: List<HeroItem>) {
    val itemsJson = items.map { hero ->
        JSONObject(mapOf("name" to hero.name, "section" to hero.section))
    }
        .toString()
    File(assetsDatabase + "items.json").writeText(itemsJson)
}

private fun loadItemsOneByOne(locale: LOCALE, loadImages: Boolean = false) {
    JSONObject().apply {
        val list = getItemsUseCase(locale)
//        .filter { it.name == "Swift Blink" }

        for (item in list) {
            val url = locale.baseUrl + item.href
            val root = try {
                Jsoup.connect(url).get()
            } catch (e: Exception) {
                println("could parse $url error: $e")
                continue
            }

            JSONObject().apply {
                loadName(root)
                loadDescription(root)
                loadAdditionalInformation(root)
                loadAbilities(root)
                loadTips(root)
                loadFacts(root)
                loadLore(root)
                loadCostAndBoughtFrom(root, item)
                loadRecipe(root)
                loadBonuses(root)

                if (loadImages) {
                    val url = root.getElementById("itemmainimage").child(0).attr("href")
                    saveImage(url, "$assetsDatabaseItems/${item.name}", "full")
                }
                saveFile("$assetsDatabaseItems/${item.name}/${locale.directory}", "description.json", toJSONString())
            }
        }
    }
}

private fun JSONObject.loadBonuses(root: Document) {
//    val table = root.getElementsByAttributeValue("style", "text-align:left;")[0].child(0).children()
//    var array: JSONArray? = null
//    for (row in table) {
//        val haveBonuses = row.child(0).text().startsWith("Bonus") || row.child(0).text().startsWith("Бонус")
//        if (haveBonuses) {
//            array = JSONArray()
//            val bonuses = row.child(2).text().split(Regex("(?=\\+)")).drop(1).map { it.trim() }
//            bonuses.forEach { array.add(it) }
//        }
//        put("bonuses", array)
//    }
}

private fun JSONObject.loadRecipe(root: Document) {
    val table = root.getElementsByAttributeValue("style", "text-align:center;").firstOrNull() ?: return
    val children = table.getElementsByAttributeValue("width", "40")
    val upgradeInList = JSONArray()
    val containsFromList = JSONArray()
    var findMatchWithItemName = false
    for (child in children) {
        val recipeName = child.attr("alt").substringBefore("(").trim()
        if (this["name"] == recipeName) {
            findMatchWithItemName = true
            continue
        }
        if (findMatchWithItemName) {
            if (recipeName == "Рецепт") containsFromList.add("Recipe")
            else containsFromList.add(recipeName)
        } else {
            upgradeInList.add(recipeName)
        }
    }
    put("upgradeIn", if (upgradeInList.size > 0) upgradeInList else null)
    put("consistFrom", if (containsFromList.size > 0) containsFromList else null)
}

private fun JSONObject.loadCostAndBoughtFrom(root: Document, item: HeroItem) {
    val cost = try {
        val table = root.getElementsByClass("infobox")[0]
        val style = "width:200px; background-color:#DAA520; color:#fff; text-shadow:1px 1px 2px #000;"
        val elements = table.getElementsByAttributeValue("style", style)
        (elements[0].childNodes()[0].childNodes()[0] as TextNode).text().trim()
    } catch (e: Exception) {
        println("Could load cost for ${item.name}")
        null
    }
    put("cost", cost)
//    val place = (table.getElementsByAttributeValue("style", "width:50%;")[0].childNodes().lastOrNull() as? TextNode)?.text()?.trim()
//    put("boughtFrom", place)
}

private fun JSONObject.loadFacts(root: Document) {
    var children = root.getElementById("mw-content-text").child(0).children()
    if (children.size == 1) children = root.getElementById("mw-content-text").child(1).children()
    var nextSectionIsAdditionalInformation = false
    var array: JSONArray? = null
    for (child in children) {
        if (nextSectionIsAdditionalInformation) {
            array = JSONArray()
            val additionalInformation = child.getElementsByTag("li")
            for (item in additionalInformation) array.add(item.text())
            nextSectionIsAdditionalInformation = false
        }

        if (child.tag().toString() == "h2") {
            nextSectionIsAdditionalInformation = when (child.child(0).id()) {
                "Trivia", "Факты" -> true
                else -> false
            }
        }
    }
    put("trivia", array)
}

private fun JSONObject.loadTips(root: Document) {
    var children = root.getElementById("mw-content-text").child(0).children()
    if (children.size == 1) children = root.getElementById("mw-content-text").child(1).children()
    var nextSectionIsAdditionalInformation = false
    var array: JSONArray? = null
    for (child in children) {
        if (nextSectionIsAdditionalInformation) {
            array = JSONArray()
            val additionalInformation = child.getElementsByTag("li")
            for (item in additionalInformation) array.add(item.text())
            nextSectionIsAdditionalInformation = false
        }

        if (child.tag().toString() == "h2") {
            nextSectionIsAdditionalInformation = when (child.child(0).id()) {
                "Tips", "Советы" -> true
                else -> false
            }
        }
    }
    put("tips", array)
}

private fun JSONObject.loadLore(root: Document) {
    var children = root.getElementById("mw-content-text").child(0).children()
    if (children.size == 1) children = root.getElementById("mw-content-text").child(1).children()
    var nextSectionIsAdditionalInformation = false
    var array: JSONArray? = null
    for (child in children) {
        if (nextSectionIsAdditionalInformation) {
            array = JSONArray()
            val additionalInformation = child.getElementsByTag("li")
            for (item in additionalInformation) {
                array.add(item.text().replace("\\[.+?]".toRegex(), "").trim())
            }
            nextSectionIsAdditionalInformation = false
        }

        if (child.tag().toString() == "h2") {
            nextSectionIsAdditionalInformation = when (child.child(0).id()) {
                "Lore", "История" -> true
                else -> false
            }
        }
    }
    put("lore", array)
}

private fun JSONObject.loadAbilities(root: Document) {
    val abilities = root.getElementsByAttributeValue("style", "display:flex; flex-wrap:wrap; align-items:flex-start;")
    JSONArray().apply {
        abilities.forEach {
            JSONObject().apply {
                val abilityName = it.getElementsByTag("div")[3].childNode(0).toString().trim()
                put("abilityName", abilityName)

                var audioUrl = it.getElementsByTag("source").attr("src")
                if (audioUrl.isNullOrEmpty()) audioUrl = null
                put("audioUrl", audioUrl)

                val effects = it.getElementsByAttributeValue("style", "font-size:98%;")
                JSONArray().apply {
                    effects.mapNotNull { if (it.text() != "") add(it.text().replace("(", "(TagAghanim")) }
                    put("effects", this)
                }

                val description = it.getElementsByTag("div")[12].text()
                put("description", description)

                val params = it.getElementsByAttributeValue("style", "display:inline-block; vertical-align:top; padding:3px 5px; border:1px solid rgba(0, 0, 0, 0);")[0].children()
                params.filter { it.attr("style").isEmpty() }.also {
                    JSONArray().apply {
                        it.forEach { add(it.text()) }
                        put("params", this)
                    }
                }

                var cooldown = it.getElementsByAttributeValue("style", "display:table-cell; margin:4px 0px 0px 0px; max-width:100%; width:240px;").getOrNull(0)
                if (cooldown == null) cooldown = it.getElementsByAttributeValue("style", "display:inline-block; margin:8px 0px 0px 50px; width:190px; vertical-align:top;").getOrNull(0)
                if (cooldown?.getElementsByAttribute("href")?.getOrNull(0)?.attr("href").equals("/Aghanim%27s_Scepter")) {
                    put("cooldown", cooldown?.text()?.replace("(", "(TagAghanim"))
                } else {
                    put("cooldown", cooldown?.text()?.replace("(", "(TagTalent"))
                }

                val mana = it.getElementsByAttributeValue("style", "display:table-cell; margin:4px 0px 0px 0px; width:240px;").getOrNull(0)
                if (mana?.getElementsByAttribute("href")?.getOrNull(0)?.attr("href").equals("/Aghanim%27s_Scepter")) {
                    put("mana", mana?.text()?.replace("(", "(TagAghanim"))
                } else {
                    put("mana", mana?.text()?.replace("(", "(TagTalent"))
                }

                val itemBehaviour = it.getElementsByAttributeValue("style", "margin-left: 50px;")
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

                    put("itemBehaviour", this)
                }

                val story = it.getElementsByAttributeValue("style", "margin-top: 5px; padding-top: 2px; border-top: 1px solid #C1C1C1;").getOrNull(0)
                put("story", story?.text())

                val notesBlock = it.getElementsByAttributeValue("style", "flex:1 1 450px; word-wrap:break-word;").getOrNull(0)
                val notes = notesBlock?.getElementsByTag("li")
                JSONArray().apply {
                    notes?.forEach {
                        add(it.text().replace("( ", "(TagTalent "))
                    }
                    put("notes", this)
                }

                add(this)
            }
        }
        put("abilities", this)
    }
}

private fun JSONObject.loadAdditionalInformation(root: Document) {
    var children = root.getElementById("mw-content-text").child(0).children()
    if (children.size == 1) children = root.getElementById("mw-content-text").child(1).children()
    var nextSectionIsAdditionalInformation = false
    for (child in children) {
        if (nextSectionIsAdditionalInformation) {
            val additionalInformation = child.getElementsByTag("li")
            JSONArray().apply {
                for (item in additionalInformation) {
                    add(item.text())
                }
                nextSectionIsAdditionalInformation = false
                put("additionalInformation", this)
            }
        }
        if (child.tag().toString() == "h2") {
            when (child.child(0).id()) {
                "Additional_Information", "Дополнительная_информация" -> {
                    nextSectionIsAdditionalInformation = true
                }
            }
        }
    }
}

private fun JSONObject.loadDescription(root: Document) {
    put("description", root.getElementsByClass("infobox")[0].child(0).child(2).text())
}

private fun JSONObject.loadName(root: Document) {
    val name = root.getElementById("firstHeading").text()
    put("name", name)
}

private fun JSONArray.ifContainAdd(alt: String, spellImmunityBlockPartial: String, it: Element) {
    if (alt.contains(spellImmunityBlockPartial)) {
        add(
            "(${spellImmunityBlockPartial.dropLast(4)})^"
                    + it.getElementsByAttribute("title")[0].attr("title").dropLast(1) + "\n"
                    + it.text().replace(". ", "\n")
        )
    }
}




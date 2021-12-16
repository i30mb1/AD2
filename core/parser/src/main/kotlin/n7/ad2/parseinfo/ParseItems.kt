package n7.ad2.parseinfo

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.io.File

private const val assetsPathToItem = "items/"

private class HeroItem(val name: String, val href: String, val section: String)

private fun HeroItem.toJsonObject() = JSONObject(mapOf("name" to name, "section" to section))

fun String.connect(): Document {
    return Jsoup.connect(this).get()
}

fun main() {
    loadItemsJsonFile()

    loadItemsOneByOne(LOCALE.EN, false)
    loadItemsOneByOne(LOCALE.RU, false)
}

enum class LOCALE(val urlAllItems: String, val baseUrl: String, val directory: String) {
    RU("https://dota2-ru.gamepedia.com/%D0%9F%D1%80%D0%B5%D0%B4%D0%BC%D0%B5%D1%82%D1%8B", "https://dota2-ru.gamepedia.com", "ru"),
    EN("https://dota2.gamepedia.com/Items", "https://dota2.gamepedia.com", "en")
}

private fun loadItemsOneByOne(locale: LOCALE, loadImages: Boolean = false) {
    JSONObject().apply {
        val list = getItems(LOCALE.EN.urlAllItems.connect())

        for (item in list) {
            val root = try {
                (locale.baseUrl + item.href).connect()
            } catch (e: Exception) {
                println("could parse ${locale.baseUrl + item.href}")
                continue
            }
            val folderForItemsDescription = assetsPathToItem + item.name + "/" + locale.directory

            JSONObject().apply {
                loadName(root)
                loadDescription(root)
                loadAdditionalInformation(root)
                loadAbilities(root)
                loadTips(root)
                loadFacts(root)
                loadLore(root)
                loadCostAndBoughtFrom(root)
                loadRecipe(root)
                loadBonuses(root)

                if (loadImages) saveImage(root.getElementById("itemmainimage").getElementsByTag("img").attr("src"), assetsPathToItem + item.name + "/", "full")

                createFolderInAssets(folderForItemsDescription)
                saveFileWithDataInAssets("$folderForItemsDescription/description.json", toJSONString())
            }
        }
    }

}

private fun JSONObject.loadBonuses(root: Document) {
    val table = root.getElementsByAttributeValue("style", "text-align:left;")[0].child(0).children()
    var array: JSONArray? = null
    for (row in table) {
        val haveBonuses = row.child(0).text().startsWith("Bonus") || row.child(0).text().startsWith("Бонус")
        if (haveBonuses) {
            array = JSONArray()
            val bonuses = row.child(1).text().split(Regex("(?=\\+)")).drop(1).map { it.trim() }
            bonuses.forEach { array.add(it) }
        }
        put("bonuses", array)
    }
}

private fun JSONObject.loadRecipe(root: Document) {
    val table = root.getElementsByAttributeValue("style", "text-align:left;")[0].child(0).children()
    val children = table.last().getElementsByAttributeValue("width", "40")
    val upgradeInList = JSONArray()
    val containsFromList = JSONArray()
    var findMatchWithItemName = false
    for (child in children) {
        val recipeName = child.attr("alt").removeBrackets()
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

private fun JSONObject.loadCostAndBoughtFrom(root: Document) {
    val table = root.getElementsByClass("infobox")[0]
    var cost = (table.getElementsByAttributeValue("style", "width:50%; background-color:#DAA520;")[0].childNodes().lastOrNull() as? TextNode)?.text()?.removeBrackets()
    if (cost == ")") cost = (table.getElementsByAttributeValue("style", "width:50%; background-color:#DAA520;")[0].childNodes().get(2) as? TextNode)?.text()?.removeBrackets()?.split(" ")?.get(0)
    put("cost", cost)
    val place = (table.getElementsByAttributeValue("style", "width:50%;")[0].childNodes().lastOrNull() as? TextNode)?.text()?.trim()
    put("boughtFrom", place)
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
    val abilities = root.getElementsByAttributeValue("style", "display: flex; flex-wrap: wrap; align-items: flex-start;")
    JSONArray().apply {
        abilities.forEach {
            JSONObject().apply {
                val abilityName = it.getElementsByTag("div")[3].childNode(0).toString().trim()
                put("abilityName", abilityName)

                var audioUrl = it.getElementsByTag("source").attr("src")
                if (audioUrl.isNullOrEmpty()) audioUrl = null
                put("audioUrl", audioUrl)

                val effects = it.getElementsByAttributeValue("style", "display: inline-block; width: 32%; vertical-align: top;")
                JSONArray().apply {
                    effects.mapNotNull { if (it.text() != "") add(it.text().replace("(", "(TagAghanim")) }
                    put("effects", this)
                }

                val description = it.getElementsByTag("div")[12].text()
                put("description", description)

                val params = it.getElementsByAttributeValue("style", "vertical-align:top; padding: 3px 5px; display:inline-block;")[0].children()
                params.filter { it.attr("style").isEmpty() }.also {
                    JSONArray().apply {
                        it.forEach { add(it.text()) }
                        put("params", this)
                    }
                }

                var cooldown = it.getElementsByAttributeValue("style", "display:inline-block; margin:8px 0px 0px 50px; width:370px; vertical-align:top;").getOrNull(0)
                if (cooldown == null) cooldown = it.getElementsByAttributeValue("style", "display:inline-block; margin:8px 0px 0px 50px; width:190px; vertical-align:top;").getOrNull(0)
                if (cooldown?.getElementsByAttribute("href")?.getOrNull(0)?.attr("href").equals("/Aghanim%27s_Scepter")) {
                    put("cooldown", cooldown?.text()?.replace("(", "(TagAghanim"))
                } else {
                    put("cooldown", cooldown?.text()?.replace("(", "(TagTalent"))
                }

                val mana = it.getElementsByAttributeValue("style", "display:inline-block; margin:8px 0px 0px; width:190px; vertical-align:top;").getOrNull(0)
//                if (mana == null) mana =
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

                val notesBlock = it.getElementsByAttributeValue("style", "flex: 1 1 450px; word-wrap: break-word;").getOrNull(0)
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
    put("name", root.getElementsByClass("firstHeading").text())
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

private fun loadItemsJsonFile(locale: LOCALE = LOCALE.EN) {
    val root = locale.urlAllItems.connect()
    val items: List<JSONObject> = getItems(root).map { it.toJsonObject() }

    val file = File(assetsDatabase + "items.json")
    file.writeText(items.toString())
}

private fun getItems(root: Document): List<HeroItem> {
    val ignoreList = listOf("Helm of the Dominator 1", "Helm of the Dominator 2")
    val result = mutableListOf<HeroItem>()
    var findItemSection = false
    var itemSection = ""

    val elements = root.getElementById("mw-content-text")?.allElements ?: throw Exception("could find elements")
    for (element in elements) {
        if (element.tag().toString() == "h2" && element.children().size > 0 && element.child(0).id().toString() == "Items") findItemSection = true
        if (element.tag().toString() == "h2" && element.children().size > 0 && element.child(0).id().toString() == "Event_Items") findItemSection = false
        if (element.tag().toString() == "h3") itemSection = element.text()
        if (findItemSection) {
            if (element.tag().toString() == "div") {
                for (item in element.children()) {
                    if (item.children().size < 2) continue
                    val itemHref = item.child(1).attr("href") ?: throw Exception("could not find item href")
                    val itemName = item.child(1).attr("title") ?: throw Exception("could not find item name")

                    val heroItem = HeroItem(itemName, itemHref, itemSection)
                    if (!ignoreList.contains(heroItem.name)) result.add(heroItem)
                }

            }
        }
    }
    return result
}


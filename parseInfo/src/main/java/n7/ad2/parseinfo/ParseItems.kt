package n7.ad2.parseinfo

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.io.File

const val assetsFolderItem = "items/"
const val fileName = "items.json"

fun String.connect():Document {
    return Jsoup.connect(this).get()
}

fun main() {
    loadItemsFileAndPrepareFolders()

    loadItemsOneByOne(LOCALE.EN)
    loadItemsOneByOne(LOCALE.RU)
}

enum class LOCALE(val urlAllItems: String, val baseUrl: String, val directory: String) {
    RU("https://dota2-ru.gamepedia.com/%D0%9F%D1%80%D0%B5%D0%B4%D0%BC%D0%B5%D1%82%D1%8B", "https://dota2-ru.gamepedia.com", "ru"),
    EN("https://dota2.gamepedia.com/Items", "https://dota2.gamepedia.com", "en")
}

private fun loadItemsOneByOne(locale: LOCALE) {
    JSONObject().apply {
        val list = getItems(LOCALE.EN.urlAllItems.connect())
            .filter { !it.first.contains("(") }
            .filter { it.second == "Holy Locket" }
        val description = "description.json"

        list.forEach {
            val root = (locale.baseUrl + it.first).connect()
            val folderForItemsDescription = assetsFolderItem + it.second + File.separator + locale.directory

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

                val loadImage = false
                if(loadImage) saveImage(root.getElementById("itemmainimage").getElementsByTag("img").attr("src"), assetsFolderItem + it.second + File.separator, "full")

                createFolderInAssets(folderForItemsDescription)
                File(assets + folderForItemsDescription + File.separator + description).writeText(toJSONString())
                println("item ${locale.directory}${it.first} saved")
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
        var recipeName = child.attr("alt").removeBrackets()
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
    val cost = (table.getElementsByAttributeValue("style", "width:50%; background-color:#DAA520;")[0].childNodes().lastOrNull() as? TextNode)?.text()?.removeBrackets()
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
    var array : JSONArray? = null
    for (child in children) {
        if (nextSectionIsAdditionalInformation) {
            array = JSONArray()
            val additionalInformation = child.getElementsByTag("li")
            for (item in additionalInformation) {
                array.add(item.text().replace("\\[.+?]".toRegex(),"").trim())
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
                        it.forEach {
                            if (it.getElementsByAttribute("href").getOrNull(0)?.attr("href").equals("/Aghanim%27s_Scepter")) {
                                add(it.text().replace("(", "(TagAghanim"))
                            } else {
                                add(it.text().replace("(", "(TagTalent"))
                            }
                        }
                        put("params", this)
                    }
                }

                val cooldown = it.getElementsByAttributeValue("style", "display:inline-block; margin:8px 0px 0px 50px; width:370px; vertical-align:top;").getOrNull(0)
                if (cooldown?.getElementsByAttribute("href")?.getOrNull(0)?.attr("href").equals("/Aghanim%27s_Scepter")) {
                    put("cooldown", cooldown?.text()?.replace("(", "(TagAghanim"))
                } else {
                    put("cooldown", cooldown?.text()?.replace("(", "(TagTalent"))
                }

                val mana = it.getElementsByAttributeValue("style", "display:inline-block; margin:8px 0px 0px; width:190px; vertical-align:top;").getOrNull(0)
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
                "Additional_information", "Дополнительная_информация" -> {
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

private fun loadItemsFileAndPrepareFolders(deleteOldFiles: Boolean = false) {
    if (deleteOldFiles) File(assets + assetsFolderItem).deleteRecursively()

    val root = LOCALE.EN.urlAllItems.connect()
    val finalDestination = assets + fileName

    JSONObject().apply {
        fillJsonWithItems(root)

        createFolderInAssets(assetsFolderItem)
        File(finalDestination).writeText(toJSONString())
        println("file $finalDestination loaded successfully")
    }
}

private fun getItems(root: Document): MutableList<Pair<String, String>> {
    val resultItemList = mutableListOf<Pair<String, String>>()

    var findItemSection = false
    val sections = root.getElementById("mw-content-text")!!
    for (element in sections.allElements) {
        if (element.id().toString() == "Items") findItemSection = true
        if (element.id().toString() == "Events") findItemSection = false

        if (findItemSection) {
            if (element.tag().toString() == "div") {
                for (item in element.children()) {
                    if (item.children().size < 2) continue
                    JSONObject().apply {
                        val itemHref = item.child(1).attr("href")!!
                        val itemName = item.child(1).attr("title")!!

                        resultItemList.add(Pair(itemHref, itemName))
                    }
                }

            }
        }
    }
    return resultItemList
}

private fun JSONObject.fillJsonWithItems(root: Document) {

    var findItemSection = false
    val sections = root.getElementById("mw-content-text")!!
    var itemSection = JSONArray()
    var itemSectionName = ""
    for (element in sections.allElements) {
        if (element.id().toString() == "Items") findItemSection = true
        if (element.id().toString() == "Events") {
            findItemSection = false
            if (itemSection.size != 0) put("items", itemSection)
        }

        if (findItemSection) {
            if (element.tag().toString() == "h3") {
                itemSectionName = element.text()
            }

            if (element.tag().toString() == "div") {
                for (item in element.children()) {
                    if (item.children().size < 2) continue
                    JSONObject().apply {
                        val itemName = item.child(1).attr("title")!!

                        val assetsPath = assetsFolderItem + itemName
                        createFolderInAssets(assetsPath)

                        put("nameEng", itemName)
                        put("assetsPath", assetsPath)
                        put("section", itemSectionName)

                        itemSection.add(this)
                        println("item $itemName added")
                    }
                }

            }
        }
    }
}

private fun createFolderInAssets(path: String) {
    if (File(assets + path).exists()) return
    File(assets + path).mkdirs()
    println("folder was created to $path")
}
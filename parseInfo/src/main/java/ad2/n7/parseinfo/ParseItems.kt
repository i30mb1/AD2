package ad2.n7.parseinfo

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

val assets = System.getProperty("user.dir") + "\\app\\src\\main\\assets\\"
const val assetsFolderItem = "items\\"
const val fileName = "items.json"

fun String.connect():Document {
    return Jsoup.connect(this).get()
}

fun main() {
//    loadItemsFileAndPrepareFolders()

    loadItemsOneByOne(LOCALE.EN)
    loadItemsOneByOne(LOCALE.RU)
}
enum class LOCALE(val urlAllItems: String, val baseUrl: String, val directory: String) {
    RU("https://dota2-ru.gamepedia.com/%D0%9F%D1%80%D0%B5%D0%B4%D0%BC%D0%B5%D1%82%D1%8B", "https://dota2-ru.gamepedia.com", "ru"),
    EN("https://dota2.gamepedia.com/Items", "https://dota2.gamepedia.com", "en")
}

private fun loadItemsOneByOne(locale: LOCALE) {
    JSONObject().apply {
        val list = getItems(LOCALE.EN.urlAllItems.connect()).take(4)
        val description = "description.json"

        list.forEach {
            val root = (locale.baseUrl + it.first).connect()
            val folderForItemsDescription =assetsFolderItem + it.second + File.separator + locale.directory

            JSONObject().apply {
                put("name", root)

                createFolderInAssets(folderForItemsDescription)
                File(assets + folderForItemsDescription + File.separator + description).writeText(toJSONString())
            }

        }
    }

}

private fun loadItemsFileAndPrepareFolders(deleteOldFiles: Boolean = false) {
    if (deleteOldFiles) File(assets + assetsFolderItem).deleteRecursively()

    val root = LOCALE.EN.urlAllItems.connect()
    val finalDestination = assets  + fileName

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
            if (itemSection.size != 0) put(itemSectionName, itemSection)
        }

        if (findItemSection) {
            if (element.tag().toString() == "h3") {
                if (itemSection.size != 0) {
                    put(itemSectionName, itemSection)
                    println("section $itemSectionName added")
                }
                itemSectionName = element.text()
                itemSection = JSONArray()
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
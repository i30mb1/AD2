package ad2.n7.parseinfo

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

val assetsFilePath = System.getProperty("user.dir") + "\\app\\src\\main\\assets"
const val assetsFolderItem = "items\\"
const val fileName = "items.json"

fun main() {
    loadItemsFileAndPrepareFolders()

}

enum class LOCALE(val urlAllItems: String, val baseUrl: String, val directory: String) {
    RU("https://dota2-ru.gamepedia.com/%D0%9F%D1%80%D0%B5%D0%B4%D0%BC%D0%B5%D1%82%D1%8B", "https://dota2-ru.gamepedia.com", "ru"),
    EN("https://dota2.gamepedia.com/Items", "https://dota2.gamepedia.com", "en")
}

private fun loadItemsFileAndPrepareFolders(deleteOldFiles: Boolean = false) {
    if (deleteOldFiles) File(assetsFilePath + File.separator + assetsFolderItem).deleteRecursively()

    val root = connectTo(LOCALE.EN.urlAllItems)
    val finalDestination = assetsFilePath + File.separator + fileName
    var findItemSection = false

    val sections = root.getElementById("mw-content-text")!!

    var itemSection = JSONArray()
    var itemSectionName = ""

    JSONObject().apply {
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
                            val itemHref = item.child(1).attr("href")!!
                            val itemName = item.child(1).attr("title")!!

                            put("nameEng", itemName)
                            val assetsPath = assetsFolderItem + itemName
                            put("assetsPath", assetsPath)
                            itemSection.add(this)
                            println("item $itemName added")

                            createFolderInAssets(assetsPath)
                        }
                    }

                }
            }
        }

        createFolderInAssets(assetsFolderItem)
        File(finalDestination).writeText(toJSONString())
        println("file $finalDestination loaded successfully")
    }
}


private fun connectTo(url: String): Document {
    return Jsoup.connect(url).get()
}

private fun createFolderInAssets(path: String) {
    if (File(assetsFilePath + File.separator + path).exists()) return
    File(assetsFilePath + File.separator + path).mkdirs()
}
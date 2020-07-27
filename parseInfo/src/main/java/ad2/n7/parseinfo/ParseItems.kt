package ad2.n7.parseinfo

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

val assetsFilePath = System.getProperty("user.dir") + "\\app\\src\\main\\assets"

fun main() {



}

enum class LOCALE(val urlAllHeroes: String, val baseUrl: String, val directory: String) {
    RU("https://dota2-ru.gamepedia.com/%D0%9F%D1%80%D0%B5%D0%B4%D0%BC%D0%B5%D1%82%D1%8B", "https://dota2-ru.gamepedia.com", "ru"),
    EN("https://dota2.gamepedia.com/Items", "https://dota2.gamepedia.com", "en")
}

private fun connectTo(url: String): Document {
    return Jsoup.connect(url).get()
}

private fun createItemFolderInAssets(path: String) {
    File(assetsFilePath + File.separator + path).mkdirs()
}
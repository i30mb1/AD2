package n7.ad2.parseinfo

import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

internal val assetsDatabase = System.getProperty("user.dir") + "/core/database/src/main/assets"
internal val assetsDatabaseItems = "$assetsDatabase/items"
internal val assetsDatabaseSpells = "$assetsDatabase/images"
internal val assetsDatabaseHeroes = "$assetsDatabase/heroes"

internal fun String.removeBrackets(): String {
    return removeSurrounding("(", ")")
}

internal fun saveImage(url: String, path: String, name: String) {
    saveImageInternal(url, path.lowercase().replace(" ", "_"), name)
}

private fun saveImageInternal(url: String, path: String, name: String) {
    val bufferImageIO = ImageIO.read(URL(url))
    val directory = File(path)
    directory.mkdirs()
    val file = File(path, "$name.png")
    val fileWebp = File(path, "$name.webp")
    if (file.exists() || fileWebp.exists()) return
    file.createNewFile()
    ImageIO.write(bufferImageIO, "png", file)
    println("image '$path' saved")
}

internal fun saveFile(path: String, fileName: String, text: String) {
    saveFileInternal(path.lowercase().replace(" ", "_"), fileName, text)
}

private fun saveFileInternal(path: String, fileName: String, text: String) {
    val directory = File(path)
    directory.mkdirs()
    val file = File(path, fileName)
    file.createNewFile()
    file.writeText(text)
    println("file (${file.length()} bytes) saved in '$path'")
}

fun String.toCamelCase(delimiter: String = " "): String {
    return split(delimiter).joinToString(delimiter) { word ->
        word.replaceFirstChar(Char::titlecaseChar)
    }
}

internal fun getTextFromNodeFormatted(element: Node): String {
    val text = try {
        getTextFromNode(element)
    } catch (e: DownloadImagePlease) {
        saveImage(e.url, assetsDatabaseSpells, e.name)
        getTextFromNode(element)
    }
    return text.removeSuffix(".").trim().replace(". ", "\n ")
}

data class Image(val path: String, val name: String)

val availableImagesSpells = File(assetsDatabaseSpells).listFiles()?.map {
    val name = it.name.substringBefore(".")
    val formattedName = "[${name.replace("_", " ")}]"
    val path = it.path.substringAfter("assets\\").replace("\\", "/")
    Image(path, name)
} ?: emptyList()

val availableImagesItems = File(assetsDatabaseItems).listFiles()?.map { file ->
    val name = file.name
    val formattedName = "[${name.replace("_", " ")}]"
    val path = file.path.substringAfter("assets\\").replace("\\", "/") + "/full.webp"
    Image(path, name)
} ?: emptyList()
val availableImages = availableImagesSpells + availableImagesItems

class DownloadImagePlease(
    val name: String,
    val url: String,
) : Exception("не нашли картинку $name, скачай ее и положи в папку! вот ссылка $url")

fun getTextFromNode(element: Node): String {
    if (element is TextNode) {
        return element.text()
    } else {
        val url = element.attr("data-src")
        val name = element.attr("data-image-key")
            .substringBefore(".")
            .removeSuffix("_icon")
            .replace("%27", "'")
            .lowercase()
        val item = availableImages.find { it.name == name }
        if (item == null && name.isNotEmpty()) throw DownloadImagePlease(name, url)
        val span = if (item != null) "<span image=\"${item.path}\">[picture:${item.name}]</span>" else ""
        return element.childNodes().fold(span) { value: String, node: Node ->
            value + getTextFromNode(node)
        }
    }
}
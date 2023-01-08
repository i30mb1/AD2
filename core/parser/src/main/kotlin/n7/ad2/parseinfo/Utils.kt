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
    return getTextFromNode(element).removeSuffix(".").trim().replace(". ", "\n ")
}

data class Image(val path: String, val name: String, val formattedName: String)

val availableImagesSpells = File(assetsDatabaseSpells).listFiles()?.map {
    val name = it.name.substringBefore(".")
    val formattedName = "[${name.replace("_", " ")}]"
    val path = it.path.substringAfter("assets\\").replace("\\", "/")
    Image(path, name, formattedName)
} ?: emptyList()

val availableImagesItems = File(assetsDatabaseItems).listFiles()?.map {
    val name = it.name.toCamelCase("_")
    val formattedName = "[${name.replace("_", " ")}]"
    val path = it.path.substringAfter("assets\\").replace("\\", "/") + "/full.webp"
    Image(path, name, formattedName)
} ?: emptyList()
val availableImages = availableImagesSpells + availableImagesItems

fun getTextFromNode(element: Node): String {
    if (element is TextNode) {
        return element.text()
    } else {
        val attr = element.attr("data-image-key").substringBefore(".").removeSuffix("_icon").replace("%27", "'")
        val item = availableImages.find { it.name == attr }
        val span = if (item != null) "<span image=\"${item.path}\">${item.formattedName}</span>" else ""
//        else if (attr.isNotEmpty()) error("не нашли картинку [$attr]")
        return element.childNodes().fold(span) { value: String, node: Node ->
            value + getTextFromNode(node)
        }
    }
}
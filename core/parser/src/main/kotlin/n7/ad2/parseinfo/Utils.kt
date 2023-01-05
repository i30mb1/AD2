package n7.ad2.parseinfo

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

internal val assetsDatabase = System.getProperty("user.dir") + "/core/database/src/main/assets"
internal val assetsDatabaseItems = "$assetsDatabase/items"
internal val assetsDatabaseItemsImages = "$assetsDatabase/images"
internal val assetsDatabaseHeroes = "$assetsDatabase/heroes"

internal fun String.removeBrackets(): String {
    return removeSurrounding("(", ")")
}

internal fun saveImage(url: String, path: String, name: String) {
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
    val directory = File(path)
    directory.mkdirs()
    val file = File(path, fileName)
    file.createNewFile()
    file.writeText(text)
    println("file (${file.length()} bytes) saved in '$path'")
}


internal fun getTextFromNodeFormatted(element: Node): String {
    return getTextFromNode(element).removeSuffix(".").trim().replace(". ", "\n")
}

data class Image(val path: String, val name: String, val formattedName: String)

val availableImages = File(assetsDatabaseItemsImages)?.listFiles()?.map {
    val name = it.name.substringBefore(".")
    val formattedName = "[${name.replace("_", " ")}]"
    val path = it.path.substringAfter("assets\\").replace("\\", "/")
    Image(path, name, formattedName)
} ?: emptyList()

private fun getTextFromNode(element: Node): String {
    val result = StringBuilder()
    if (element is TextNode) {
        return element.text()
    } else if (element is Element) {
        val attr = element.attr("data-image-key").substringBefore(".").removeSuffix("_icon")
        val item = availableImages.find { it.name == attr }
        if (item != null) result.append("<span image=\"${item.path}\">${item.formattedName}</span>")
        element.childNodes().forEach { node ->
            result.append(getTextFromNode(node))
        }
    }
    return result.toString()
}
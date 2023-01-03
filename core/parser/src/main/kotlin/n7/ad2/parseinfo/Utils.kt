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
    return getTextFromNode(element).removeSuffix(".").trim()
}

val availableImages = File(assetsDatabaseItemsImages)?.listFiles()?.map { it.name.removeSuffix(".webp") } ?: emptyList()

private fun getTextFromNode(element: Node): String {
    val result = StringBuilder()
    if (element is Element) {
        val attr = element.attr("data-image-key").removeSuffix("_icon.png").removeSuffix(".png")
        val imageName = availableImages.find { it == attr }
        if (imageName != null) result.append("<span image=\"images/$imageName.webp\">$imageName</span>")
        element.childNodes().forEach { node ->
            result.append(getTextFromNode(node))
        }
    }
    if (element is TextNode) {
        result.append(element.text())
    }
    return result.toString()
}
package n7.ad2.parser

import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

var limits = 0

internal fun getTextFromNodeFormatted(element: Node): String {
    val text = try {
        getTextFromNode(element)
    } catch (e: DownloadImagePlease) {
        if (limits > 1000) throw Exception(e)
        limits++
        saveImage(e.url, assetsDatabaseSpells, e.name)
        ImageRepository.update()
        getTextFromNodeFormatted(element)
    }
    return text.removeSuffix(".").trim().replace(". ", "\n ")
}

private fun getTextFromNode(element: Node): String {
    val availableImages = ImageRepository.getImages()
    if (element is TextNode) {
        return element.text()
    } else {
        val url = element.attr("data-src")
        val name = element.attr("data-image-key").substringBefore(".").removeSuffix("_icon").replace("%27", "'").lowercase()
        val item = availableImages.find { it.name == name }
        if (item == null && name.isNotEmpty()) throw DownloadImagePlease(name, url)
        val span = if (item != null) "<span image=\"${item.path}\">[picture:${item.name}]</span>" else ""
        return element.childNodes().fold(span) { value: String, node: Node ->
            value + getTextFromNode(node)
        }
    }
}

internal class DownloadImagePlease(val name: String, val url: String) : Exception("не нашли картинку $name, скачай ее и положи в папку! вот ссылка $url")

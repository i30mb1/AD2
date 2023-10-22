package n7.ad2.parser.items

import n7.ad2.parser.LOCALE
import org.jsoup.Jsoup

class HeroItem(val name: String, val formattedName: String, val href: String, val section: String)

class GetItemsUseCase {

    operator fun invoke(locale: LOCALE): List<HeroItem> {
        val root = Jsoup.connect(locale.urlAllItems).get()
        val ignoreList = listOf("Helm of the Dominator 1", "Helm of the Dominator 2")
        val result = mutableListOf<HeroItem>()
        var findItemSection = false
        var itemSection = ""
        val elements = root.getElementById("mw-content-text").getElementsByClass("mw-parser-output")[0].allElements ?: error("could find elements")
        for (element in elements) {
            if (element.tag().toString() == "h2" && !findItemSection) {
                val spans = element.getElementsByAttribute("id").map { it.text() }
                val tryingToFind = listOf("Основные предметы", "Basics Items")
                if (spans.any { tryingToFind.contains(it) }) findItemSection = true
            }
            if (element.tag().toString() == "h3") {
                val spans = element.getElementsByAttribute("id").map { it.text() }
                val tryingToFind = listOf("Collectible Items", "Убранные")
                if (spans.any { tryingToFind.contains(it) }) findItemSection = false
            }
            if (element.tag().toString() == "h3") {
                itemSection = element.text()
            }
            if (findItemSection) {
                if (element.tag().toString() == "div") {
                    for (item in element.children()) {
                        if (item.children().size < 2) continue
                        val itemHref = item.child(1).attr("href") ?: throw Exception("could not find item href")
                        val formattedName = item.child(1).attr("title") ?: throw Exception("could not find item name")
                        val itemName = formattedName.replace(" ", "_").lowercase()
                        val heroItem = HeroItem(itemName, formattedName, itemHref, itemSection)
                        if (!ignoreList.contains(formattedName)) result.add(heroItem)
                    }

                }
            }
        }
        if (result.isEmpty()) error("item list is null")
        return result
    }

}

package n7.ad2.parseinfo

import org.jsoup.Jsoup

class HeroItem(val name: String, val href: String, val section: String)

class GetItemsUseCase {

    operator fun invoke(locale: LOCALE): List<HeroItem> {
        val root = Jsoup.connect(locale.urlAllItems).get()
        val ignoreList = listOf("Helm of the Dominator 1", "Helm of the Dominator 2")
        val result = mutableListOf<HeroItem>()
        var findItemSection = false
        var itemSection = ""
        val elements = root.getElementById("mw-content-text")?.allElements ?: throw Exception("could find elements")
        for (element in elements) {
            if (element.tag().toString() == "h2" && element.children().size > 0 && element.child(0).id().toString() == "Items") findItemSection = true
            if (element.tag().toString() == "h2" && element.children().size > 0 && element.child(0).id().toString() == "Event_Items") findItemSection = false
            if (element.tag().toString() == "h3") itemSection = element.text()
            if (findItemSection) {
                if (element.tag().toString() == "div") {
                    for (item in element.children()) {
                        if (item.children().size < 2) continue
                        val itemHref = item.child(1).attr("href") ?: throw Exception("could not find item href")
                        val itemName = item.child(1).attr("title") ?: throw Exception("could not find item name")

                        val heroItem = HeroItem(itemName, itemHref, itemSection)
                        if (!ignoreList.contains(heroItem.name)) result.add(heroItem)
                    }

                }
            }
        }
        return result
    }

}
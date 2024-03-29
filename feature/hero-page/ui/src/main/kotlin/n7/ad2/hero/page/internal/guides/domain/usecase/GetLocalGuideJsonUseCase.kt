package n7.ad2.hero.page.internal.guides.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import java.util.Locale
import javax.inject.Inject

private inline class HeroNameFormatted(val heroName: String)

class GetLocalGuideJsonUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    companion object {
        private fun getHeroNameFormatted(heroName: String) =
            HeroNameFormatted(heroName.lowercase(Locale.ENGLISH).replace("_", "-").replace("'", "").replace("%20", "-").replace(" ", "-"))

        private fun getUrlForHeroPage(heroName: HeroNameFormatted) = "https://ru.dotabuff.com/heroes/${heroName.heroName}"
        private fun getUrlForHeroGuides(heroName: HeroNameFormatted) = "https://www.dotabuff.com/heroes/${heroName.heroName}/guides"
    }

    suspend operator fun invoke(heroName: String): List<Nothing> = withContext(dispatchers.Default) {
        error("")
//        val heroNameFormatted = getHeroNameFormatted(heroName)
//
//        val documentWithHero = getDocumentFromUrl(getUrlForHeroPage(heroNameFormatted))
//        val easyToWinHeroesList = getHeroesThatWeakAgainstSelectedHero(documentWithHero, heroName)
//        val hardToWinHeroesList = getHeroesThatStrongAgainstSelectedHero(documentWithHero, heroName)
//        val heroWinrate = getHeroWinrate(documentWithHero)
//        val heroPopularity = getHeroPopularity(documentWithHero)
//
//        val documentWithHeroGuides = getDocumentFromUrl(getUrlForHeroGuides(heroNameFormatted))
//        val heroGuides = getHeroGuides(documentWithHeroGuides)
//
//        buildList {
//            for (heroGuide in heroGuides) {
//                add(LocalGuideJson(heroName, heroWinrate, heroPopularity, hardToWinHeroesList, easyToWinHeroesList, heroGuide))
//            }
//        }
    }
//
//    private fun getHeroGuides(document: Document): List<DetailedGuide> {
//        val guides = document.getElementsByClass("r-stats-grid")
//        if (guides.size == 0) throw Exception("could not find guides section")
//        return guides.map {
//            val guideTime = getGuideTime(it)
//            val startingItemsList = getStartingItemsList(it)
//            val heroItemsList = getHeroItemsList(it)
//            val heroSkillList = getHeroSpellList(it)
//            DetailedGuide(guideTime, startingItemsList, heroItemsList, heroSkillList)
//        }
//    }
//
//    private fun getHeroSpellList(element: Element): List<Spell> {
//        val ignoredList = listOf("")
//        val result = mutableListOf<Spell>()
//        val spells = element.getElementsByClass("kv kv-small-margin").map { it.getElementsByTag("a") }
//
//        for (spell in spells) {
//            val spellOrder = spell.getOrNull(0)?.child(1)?.text() ?: throw Exception("could not parse spellOrder")
//            var spellName = spell.getOrNull(0)?.child(0)?.attr("title") ?: throw Exception("could not parse spellName")
//            if (spellName.startsWith("Talent")) spellName = "Talent"
//            if (!ignoredList.contains(spellName)) result.add(Spell(spellName, spellOrder))
//        }
//
//        return result
//    }
//
//    private fun getHeroItemsList(element: Element): List<HeroItem> {
//        val ignoreList = listOf("Gem of True Sight")
//        val result = mutableListOf<HeroItem>()
//        val itemsGroup = element.getElementsByClass("kv r-none-mobile")
//        for (i in 1 until itemsGroup.size) {
//            val group = itemsGroup[i]
//            var itemTime = ""
//            group.getElementsByTag("small").getOrNull(0)?.let {
//                itemTime = it.text()
//                it.remove()
//            }
//            val groupImages = group.children().mapNotNull { it.getElementsByTag("img") }
//            for (image in groupImages) {
//                val itemName = image.attr("title")
//                if (!ignoreList.contains(itemName)) result.add(HeroItem(itemName, itemTime))
//            }
//        }
//        return result
//    }
//
//    private fun getGuideTime(element: Element): String {
//        val elementsTime = element.getElementsByTag("time")
//        if (elementsTime.size == 0) throw Exception("could not find guide time tag")
//        val time = elementsTime.getOrNull(0)?.attr("datetime")
//        if (time != null) return time
//        throw Exception("could not parse time")
//    }
//
//    private fun getStartingItemsList(element: Element): List<HeroItem> {
//        val ignoreList = listOf("Town Portal Scroll", "Tango (Shared)", "Observer Ward")
//        val startingItems = element.child(2).getElementsByClass("kv r-none-mobile").getOrNull(0)?.getElementsByClass("inline inline-margin")
//        if (startingItems == null || startingItems.size == 0) throw Exception("could not find starting items")
//        val result = startingItems.mapNotNull { it.getElementsByTag("img").getOrNull(0)?.attr("title") }.filterNot { ignoreList.contains(it) }.map { HeroItem(it) }
//        if (result.isEmpty()) throw Exception("could not map starting items")
//        return result
//    }
//
//    private fun detectLine(element: Element): String {
//        val elementsMid = element.getElementsByClass("fa fa-lane-midlane fa-fw lane-icon midlane-icon")
//        if (elementsMid.size == 1) return "MID LANE"
//        val elementsOff = element.getElementsByClass("fa fa-lane-offlane fa-fw lane-icon offlane-icon")
//        if (elementsOff.size == 1) return "OFF LANE"
//        val elementsSafe = element.getElementsByClass("fa fa-lane-safelane fa-fw lane-icon safelane-icon")
//        if (elementsSafe.size == 1) return "SAFE LANE"
//        val elementsRoaming = element.getElementsByClass("fa fa-lane-roaming fa-fw lane-icon roaming-icon")
//        return if (elementsRoaming.size == 1) "ROAMING" else "-"
//    }
//
//    private fun getHeroPopularity(document: Document): String {
//        val popularity = document.getElementsByClass("header-content-secondary").getOrNull(0)?.child(0)?.child(0)?.text()
//        if (popularity != null) return popularity
//        throw Exception("could not get hero popularity")
//    }
//
//    private fun getHeroWinrate(document: Document): String {
//        val winrate = document.getElementsByClass("won").getOrNull(0)?.text()
//        if (winrate != null) return winrate
//        val loserate = document.getElementsByClass("lost").getOrNull(0)?.text()
//        if (loserate != null) return loserate
//        throw Exception("could not get hero winrate")
//    }
//
//    private fun getDocumentFromUrl(url: String): Document {
//        try {
//            return Jsoup.connect(url).get()
//        } catch (e: Exception) {
//            throw Exception("could not get document from ${url.replace("dotabuff", "*******")}")
//        }
//    }
//
//
//    private fun getHeroesThatWeakAgainstSelectedHero(document: Document, heroName: String) = buildList {
//        val section = document.getElementsByTag("tbody").getOrElse(3) {
//            throw Exception("could not parse heroes that weak against $heroName")
//        }.children()
//        for (item in section) {
//            val weakHeroName = item.child(1).text().replace("Outworld Destroyer", "Outworld Devourer")
//            val heroWinrate = item.child(2).text().replace("%", "").toDouble()
//            add(HeroWithWinrate(weakHeroName, heroWinrate))
//        }
//    }
//
//    private fun getHeroesThatStrongAgainstSelectedHero(document: Document, heroName: String) = buildList {
//        val section = document.getElementsByTag("tbody").getOrElse(4) {
//            throw Exception("could not parse heroes that strong against $heroName")
//        }.children()
//        for (item in section) {
//            val strongHeroName = item.child(1).text().replace("Outworld Destroyer", "Outworld Devourer")
//            val heroWinrate = item.child(2).text().replace("%", "").toDouble()
//            add(HeroWithWinrate(strongHeroName, heroWinrate))
//        }
//    }

}
package n7.ad2.ui.heroGuide.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroGuide.domain.model.DetailedGuide
import n7.ad2.ui.heroGuide.domain.model.HeroWithWinrate
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJson
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.*
import javax.inject.Inject

class GetLocalGuideJsonUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
) {

    companion object {
        private fun getUrlForHeroPage(heroName: String) = "https://ru.dotabuff.com/heroes/$heroName"
        private fun getUrlForHeroGuides(heroName: String) = "https://www.dotabuff.com/heroes/$heroName/guides"
    }

    private fun String.formatHeroNameForUrl(): String {
        return this.toLowerCase(Locale.ENGLISH)
            .replace("_", "-")
            .replace("'", "")
            .replace("%20", "-")
            .replace(" ", "-")
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(heroName: String): LocalGuideJson = withContext(ioDispatcher) {

        val heroNameFormatted = heroName.formatHeroNameForUrl()

        val documentWithHero = getDocumentFromUrl(getUrlForHeroPage(heroNameFormatted))

        val easyToWinHeroesList = getHeroesThatWeakAgainstSelectedHero(documentWithHero, heroName)
        val hardToWinHeroesList = getHeroesThatStrongAgainstSelectedHero(documentWithHero, heroName)
        val heroWinrate = getHeroWinrate(documentWithHero)
        val heroPopularity = getHeroPopularity(documentWithHero)

        val documentWithHeroGuides = getDocumentFromUrl(getUrlForHeroGuides(heroNameFormatted))

        val guides = documentWithHeroGuides.getElementsByClass("r-stats-grid")
        val guide = guides[0]!!
        val startingItemsList = getStartingItemsList(guide)

        // FURTHER ITEMS
//        for (i in elementsItemRows.indices) {
//            var findTime = false
//            if (i == 0 && elementsItemRows[i].children().size > 2) continue
//            for (item in elementsItemRows[i].children()) {
//                if (item.tag().toString() == "div") {
//                    val itemName = item.child(0).child(0).child(0).attr("title").toLowerCase().trim { it <= ' ' }.replace(" ", "_")
//                    if (itemName == "gem_of_true_sight") continue
//                    stringFurtherItems.append(itemName)
//                    if (i != 0) if (!findTime && elementsItemRows[i].getElementsByTag("small").size > 0) {
//                        findTime = true
//                        stringFurtherItems.append("^").append(elementsItemRows[i].getElementsByTag("small")[0].text())
//                    }
//                }
//                if (!stringFurtherItems.toString().endsWith("/")) stringFurtherItems.append("/")
//            }
//        }
//
//        // SKILL BUILD
//        if (stringSkillBuilds.length != 0) {
//            stringSkillBuilds.append("+")
//        }
//        if (element.children().size >= 3) {
//            val skills = element.child(3).getElementsByClass("kv kv-small-margin")
//            for (skill in skills) {
//                var skillName = skill.child(0).child(0).child(0).attr("alt")
//                if (skillName.startsWith("Talent:")) skillName = "talent"
//                stringSkillBuilds.append(skillName).append("/")
//            }
//        }

        LocalGuideJson(
            heroName,
            heroWinrate,
            heroPopularity,
            hardToWinHeroesList,
            easyToWinHeroesList,
            DetailedGuide(
                "guideTime",
                startingItemsList,
                emptyList(),
                emptyList(),
            )
        )
    }

    private fun getGuideTime(document: Document): String {
        return document.getElementsByTag("time").text()
    }

    private fun getStartingItemsList(element: Element): List<String> {
        val ignoreList = listOf("Town Portal Scroll", "Tango (Shared)")
        val startingItems = element.child(2).getElementsByClass("kv r-none-mobile").getOrNull(0)?.getElementsByClass("inline inline-margin")
        if (startingItems == null || startingItems.size == 0) throw Exception("could not find starting items")
        val result = startingItems.mapNotNull { it.getElementsByTag("img").getOrNull(0)?.attr("title") }
            .filterNot { ignoreList.contains(it) }
        if (result.isEmpty()) throw Exception("could not map starting items")
        return result
    }

    private fun detectLine(element: Element): String {
        val elementsMid = element.getElementsByClass("fa fa-lane-midlane fa-fw lane-icon midlane-icon")
        if (elementsMid.size == 1) return "MID LANE"
        val elementsOff = element.getElementsByClass("fa fa-lane-offlane fa-fw lane-icon offlane-icon")
        if (elementsOff.size == 1) return "OFF LANE"
        val elementsSafe = element.getElementsByClass("fa fa-lane-safelane fa-fw lane-icon safelane-icon")
        if (elementsSafe.size == 1) return "SAFE LANE"
        val elementsRoaming = element.getElementsByClass("fa fa-lane-roaming fa-fw lane-icon roaming-icon")
        return if (elementsRoaming.size == 1) "ROAMING" else "-"
    }

    private fun getHeroPopularity(document: Document): String {
        val popularity = document.getElementsByClass("header-content-secondary").getOrNull(0)?.child(0)?.child(0)?.text()
        if (popularity != null) return popularity
        throw Exception("could not get hero popularity")
    }

    private fun getHeroWinrate(document: Document): String {
        val winrate = document.getElementsByClass("won").getOrNull(0)?.text()
        if (winrate != null) return winrate
        val loserate = document.getElementsByClass("lost").getOrNull(0)?.text()
        if (loserate != null) return loserate
        throw Exception("could not get hero winrate")
    }

    private fun getDocumentFromUrl(url: String): Document {
        try {
            return Jsoup.connect(url).get()
        } catch (e: Exception) {
            throw Exception("could not get document from $url")
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getHeroesThatWeakAgainstSelectedHero(document: Document, heroName: String) = buildList {
        val section = document.getElementsByTag("tbody").getOrElse(3) {
            throw Exception("could not parse heroes that weak against $heroName")
        }.children()
        for (item in section) {
            val weakHeroName = item.child(1).text().replace("Outworld Destroyer", "Outworld Devourer")
            val heroWinrate = item.child(2).text().replace("%", "").toDouble()
            add(HeroWithWinrate(weakHeroName, heroWinrate))
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getHeroesThatStrongAgainstSelectedHero(document: Document, heroName: String) = buildList {
        val section = document.getElementsByTag("tbody").getOrElse(4) {
            throw Exception("could not parse heroes that strong against $heroName")
        }.children()
        for (item in section) {
            val strongHeroName = item.child(1).text().replace("Outworld Destroyer", "Outworld Devourer")
            val heroWinrate = item.child(2).text().replace("%", "").toDouble()
            add(HeroWithWinrate(strongHeroName, heroWinrate))
        }
    }

}
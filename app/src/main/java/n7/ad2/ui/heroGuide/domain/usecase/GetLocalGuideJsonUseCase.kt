package n7.ad2.ui.heroGuide.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroGuide.domain.model.DetailedGuide
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

        val documentWithHeroGuides = getDocumentFromUrl(getUrlForHeroGuides(heroNameFormatted))

//        val popularity = pageWithHeroGuides.getElementsByAttributeValue("class", "header-content-secondary")[0].child(0).child(0).text()
//
//        // 5 DIFFERENT PACKS
//        var guideTime = ""
//        var lane = ""
//        val itemTime = mutableListOf<String>()
//        val elements = pageWithHeroGuides.getElementsByClass("r-stats-grid")
//        if (elements.size > 0) {
//            for (element in elements) {
//                if (itemTime.size > 0) break
//                guideTime = element.getElementsByTag("time").text()
//                // LANE
//                lane = detectLine(element)
////                    // ITEMS
////                    if (element.children().size >= 2) {
////                        val elementsItemRows = element.child(2).getElementsByClass("kv r-none-mobile")
//                // STARTING ITEMS
////                Element startingItems = elementsItemRows.get(0);
////                if (stringStartingItems.length() != 0) {
////                    stringStartingItems.append("+");
////                }
////                for (Element item : startingItems.children()) {
////                    if (item.tag().toString().endsWith("div")) {
////                        stringStartingItems.append(item.child(0).child(0).child(0).attr("title").toLowerCase().trim().replace(" ", "_"));
////                        stringStartingItems.append("/");
////                    }
////                }
//                // FURTHER ITEMS
////                        if (stringFurtherItems.length != 0) {
////                            stringFurtherItems.append("+")
////                        }
////                        for (i in elementsItemRows.indices) {
////                            var findTime = false
////                            if (i == 0 && elementsItemRows[i].children().size > 2) continue
////                            for (item in elementsItemRows[i].children()) {
////                                if (item.tag().toString() == "div") {
////                                    val itemName = item.child(0).child(0).child(0).attr("title").toLowerCase().trim { it <= ' ' }.replace(" ", "_")
////                                    if (itemName == "gem_of_true_sight") continue
////                                    stringFurtherItems.append(itemName)
////                                    if (i != 0) if (!findTime && elementsItemRows[i].getElementsByTag("small").size > 0) {
////                                        findTime = true
////                                        stringFurtherItems.append("^").append(elementsItemRows[i].getElementsByTag("small")[0].text())
////                                    }
////                                }
////                                if (!stringFurtherItems.toString().endsWith("/")) stringFurtherItems.append("/")
////                            }
////                        }
////                    }
//                // SKILL BUILD
////                    if (stringSkillBuilds.length != 0) {
////                        stringSkillBuilds.append("+")
////                    }
////                    if (element.children().size >= 3) {
////                        val skills = element.child(3).getElementsByClass("kv kv-small-margin")
////                        for (skill in skills) {
////                            var skillName = skill.child(0).child(0).child(0).attr("alt")
////                            if (skillName.startsWith("Talent:")) skillName = "talent"
////                            stringSkillBuilds.append(skillName).append("/")
////                        }
////                    }
//            }
//        }

        LocalGuideJson(
            heroName,
            heroWinrate,
            "",
            easyToWinHeroesList,
            hardToWinHeroesList,
            DetailedGuide(
                "guideTime",
                emptyList(),
                emptyList()
            )
        )
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
    private fun getHeroesThatWeakAgainstSelectedHero(document: Document, heroName: String) = buildList<String> {
        val section = document.getElementsByTag("tbody").getOrElse(3) {
            throw Exception("could not parse heroes that weak against $heroName")
        }.children()
        for (item in section) {
            val name = item.child(1).text()
            val heroWinrate = item.child(2).text()
            add(name)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getHeroesThatStrongAgainstSelectedHero(document: Document, heroName: String) = buildList<String> {
        val section = document.getElementsByTag("tbody").getOrElse(4) {
            throw Exception("could not parse heroes that strong against $heroName")
        }.children()
        for (item in section) {
            val name = item.child(1).text()
            val heroWinrate = item.child(2).text()
            add(name)
        }
    }

}
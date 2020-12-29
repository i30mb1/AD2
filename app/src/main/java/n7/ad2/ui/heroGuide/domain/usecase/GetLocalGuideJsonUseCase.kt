package n7.ad2.ui.heroGuide.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroGuide.domain.model.DetailedGuide
import n7.ad2.ui.heroGuide.domain.model.HeroWithWinrate
import n7.ad2.ui.heroGuide.domain.model.ItemBuild
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
        val guideTime = getGuideTime(guide)
        val heroItemsList = getHeroItemsList(guide)
        val heroSkillList = getHeroSkillList(guide)

        LocalGuideJson(
            heroName,
            heroWinrate,
            heroPopularity,
            hardToWinHeroesList,
            easyToWinHeroesList,
            DetailedGuide(
                guideTime,
                startingItemsList,
                heroItemsList,
                heroSkillList,
            )
        )
    }

    private fun getHeroSkillList(element: Element): List<String> {
        val ignoredList = listOf("")
        val result = mutableListOf<String>()
        val skills = element.getElementsByClass("kv kv-small-margin").map { it.getElementsByTag("img") }

        for (skill in skills) {
            val skillName = skill.attr("title")
            if (!ignoredList.contains(skillName)) result.add(skillName)
        }

        return result
    }

    private fun getHeroItemsList(element: Element): List<ItemBuild> {
        val ignoreList = listOf("")
        val result = mutableListOf<ItemBuild>()
        val itemsGroup = element.getElementsByClass("kv r-none-mobile")
        for (i in 1 until itemsGroup.size) {
            val group = itemsGroup[i]
            var itemTime = ""
            group.getElementsByTag("small").getOrNull(0)?.let {
                itemTime = it.text()
                it.remove()
            }
            val groupImages = group.children().mapNotNull { it.getElementsByTag("img") }
            for (image in groupImages) {
                val itemName = image.attr("title")
                if (!ignoreList.contains(itemName)) result.add(ItemBuild(itemName, itemTime))
            }
        }
        return result
    }

    private fun getGuideTime(element: Element): String {
        val elementsTime = element.getElementsByTag("time")
        if (elementsTime.size == 0) throw Exception("could not find guide time tag")
        val time = elementsTime.getOrNull(0)?.attr("datetime")
        if (time != null) return time
        throw Exception("could not parse time")
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
@file:Suppress("BlockingMethodInNonBlockingContext")

package ad2.n7.parseinfo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.File

class ParseHeroes : CoroutineScope by (CoroutineScope(Dispatchers.IO)) {
    private val assetsFilePath = System.getProperty("user.dir") + "\\app\\src\\main\\assets"

    private fun getHeroes(document: Document): Elements {
        val heroesTable = document.getElementsByAttributeValue("style", "text-align:center")[0]
        return heroesTable.getElementsByAttributeValue("style", "width:150px; height:84px; display:inline-block; overflow:hidden; margin:1px")
    }

    private fun getHeroName(element: Element): String {
        return element.getElementsByTag("a")[0].attr("title")
    }

    private fun getHeroHref(element: Element): String {
        return element.getElementsByTag("a")[0].attr("href")
    }

    private fun connectTo(url: String): Document {
        return Jsoup.connect(url).get()
    }

    fun loadHeroesNameInFile(withZh: Boolean = false, createFolders: Boolean = false, checkConnect: Boolean = false) = launch {
        val heroesEngUrl = "https://dota2.gamepedia.com/Heroes"
        val heroesZhUrl = "https://dota2-zh.gamepedia.com/Heroes"
        val fileName = "heroesNew.json"

        val rootEng = connectTo(heroesEngUrl)
        val rootZh = connectTo(heroesZhUrl)

        JSONObject().apply {
            JSONArray().apply {

                val heroesEng = getHeroes(rootEng)
                val heroesZh = getHeroes(rootZh)

                heroesEng.forEachIndexed { index, _ ->
                    val heroObject = JSONObject().apply {
                        val heroName = getHeroName(heroesEng[index])
                        val heroHrefEng = getHeroHref(heroesEng[index])

                        put("nameEng", heroName)
                        put("hrefEng", heroHrefEng)
                        if (withZh) put("nameZh", getHeroName(heroesZh[index]))
                        if (withZh) put("hrefZh", getHeroHref(heroesZh[index]))
                        val directory = "heroes2" + File.separator + heroName
                        put("assetsPath", directory)
                        if (createFolders) createHeroFolderInAssets(directory)
                        loadHeroInfo(heroHrefEng)
                    }
                    add(heroObject)
                }
                put("heroes", this)
            }
            File(assetsFilePath + File.separator + fileName).writeText(toJSONString())
        }
    }

    private fun loadHeroInfo(heroPath: String) {
        val heroUrlEng = "https://dota2.gamepedia.com$heroPath"
        if (!checkConnectToHero(heroUrlEng)) return

        val root = connectTo(heroUrlEng)


    }

    private fun checkConnectToHero(url: String): Boolean {
        return try {
            connectTo(url)
            println("connect to $url success")
            true
        } catch (e: Exception) {
            println("connect to $url fail")
            false
        }
    }

    private fun createHeroFolderInAssets(path: String) {
        File(assetsFilePath + File.separator + path).mkdirs()
    }
}

fun main() = runBlocking {
    val parse = ParseHeroes()
    parse.loadHeroesNameInFile().join()
}


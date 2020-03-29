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

class Parse : CoroutineScope by (CoroutineScope(Dispatchers.IO)) {
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


    fun loadHeroesNameInFile(withZh: Boolean = false) = launch {
        val heroesEngUrl = "https://dota2.gamepedia.com/Heroes"
        val heroesZhUrl = "https://dota2-zh.gamepedia.com/Heroes"
        val fileName = "heroesNew.json"

        val rootEng = Jsoup.connect(heroesEngUrl).get()
        val rootZh = Jsoup.connect(heroesZhUrl).get()

        JSONObject().apply {
            JSONArray().apply {

                val heroesEng = getHeroes(rootEng)
                val heroesZh = getHeroes(rootZh)

                heroesEng.forEachIndexed { index, _ ->
                    val heroObject = JSONObject().apply {
                        val heroName = getHeroName(heroesEng[index])

                        put("nameEng", heroName)
                        put("hrefEng", getHeroHref(heroesEng[index]))
                        if (withZh) put("nameZh", getHeroName(heroesZh[index]))
                        if (withZh) put("hrefZh", getHeroHref(heroesZh[index]))
                        val directory = "heroes2" + File.separator + heroName
                        put("assetsPath", directory)
                        createHeroFolderInAssets(directory)
                    }
                    add(heroObject)
                }
                put("heroes", this)
            }
            File(assetsFilePath + File.separator + fileName).writeText(toJSONString())
        }
    }

    private fun createHeroFolderInAssets(path: String) {
        File(assetsFilePath + File.separator + path).mkdirs()
    }
}

fun main() = runBlocking {
    val parse = Parse()
    parse.loadHeroesNameInFile().join()
}


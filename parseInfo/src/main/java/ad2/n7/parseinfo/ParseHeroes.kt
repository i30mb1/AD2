@file:Suppress("BlockingMethodInNonBlockingContext")

package ad2.n7.parseinfo

import ad2.n7.parseinfo.ParseHeroes.Companion.parser
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
import java.net.URL
import javax.imageio.ImageIO

class ParseHeroes private constructor(
        private val createHeroesFiles: Boolean,
        private val loadHeroFullImage: Boolean
) : CoroutineScope by (CoroutineScope(Dispatchers.IO)) {

    private constructor(builder: Builder) : this(
            builder.createHeroesFiles,
            builder.loadHeroFullImage
    )

    companion object {
        inline fun parser(block: Builder.() -> Unit) = Builder().apply(block).build()

        class Builder {
            var createHeroesFiles: Boolean = false
            var loadHeroFullImage: Boolean = false

            fun build() = ParseHeroes(this)
        }
    }

    suspend fun start() {
        if (createHeroesFiles) loadHeroesNameInFile().join()
    }

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

    private fun loadHeroesNameInFile(withZh: Boolean = false, checkConnect: Boolean = false) = launch {
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
                        createHeroFolderInAssets(directory)
                        loadHero(heroHrefEng, directory)
                    }
                    add(heroObject)
                }
                put("heroes", this)
            }
            File(assetsFilePath + File.separator + fileName).writeText(toJSONString())
        }
    }

    private fun loadHero(heroPath: String, directory: String) {
        val heroUrlEng = "https://dota2.gamepedia.com$heroPath"
        if (!checkConnectToHero(heroUrlEng)) return

        val root = connectTo(heroUrlEng)

        if (loadHeroFullImage) loadHeroImageFull(root, directory)
        loadHeroInformation(root, directory)
    }

    private fun loadHeroInformation(root: Document, directory: String) {
        JSONObject().apply {
            val description = root.getElementsByTag("p")[0].text()
            put("description", description)



            File(assetsFilePath + File.separator + directory + File.separator + "description.json").writeText(toJSONString())
        }
    }

    private fun loadHeroImageFull(root: Document, directory: String) {
        try {
            val imageUrl = root.getElementsByTag("img")[0].attr("src")
            val bufferImageIO = ImageIO.read(URL(imageUrl))
            val file = File(assetsFilePath + File.separator + directory + File.separator + "full.png")
            file.mkdirs()
            ImageIO.write(bufferImageIO, "png", file)
            println("image full saved")
        } catch (e: Exception) {
            println("image full not saved")
        }
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
    parser {
        createHeroesFiles = true
        loadHeroFullImage = false
    }.start()
}


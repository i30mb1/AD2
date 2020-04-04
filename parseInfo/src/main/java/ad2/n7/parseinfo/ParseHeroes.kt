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

// Builder Pattern https://medium.com/mindorks/builder-pattern-vs-kotlin-dsl-c3ebaca6bc3b
class ParseHeroes private constructor(
        private val createHeroesFiles: Boolean,
        private val loadHeroImage: Boolean
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

    private fun loadHeroesNameInFile(withZh: Boolean = false) = launch {
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

        if (loadHeroImage) loadHeroImageFull(root, directory)
        if (loadHeroImage) loadHeroImageMinimap(root, directory)
        loadHeroInformation(root, directory)
    }

    private fun loadHeroImageMinimap(root: Document, directory: String) {
        try {
            val imageUrl = root.getElementsByTag("img")[4].attr("src")
            val bufferImageIO = ImageIO.read(URL(imageUrl))
            val file = File(assetsFilePath + File.separator + directory + File.separator + "minimap.png")
            file.mkdirs()
            ImageIO.write(bufferImageIO, "png", file)
            println("image minimap saved")
        } catch (e: Exception) {
            println("image minimap not saved")
        }
    }

    private fun loadHeroInformation(root: Document, directory: String) {
        JSONObject().apply {
            loadDescription(root)
            loadHistory(root)
            loadAbilities(root)
            loadTrivia(root)

            File(assetsFilePath + File.separator + directory + File.separator + "description.json").writeText(toJSONString())
        }
    }

    private fun JSONObject.loadTrivia(root: Document) {
        val children = root.getElementById("mw-content-text").child(0).children()
        var nextSectionIsTips = false
        for (child in children) {
            if (nextSectionIsTips) {
                val trivias = child.getElementsByTag("li")
                JSONArray().apply {
                    for (trivia in trivias) {
                        add(trivia.text())
                    }
                    nextSectionIsTips = false

                    put("trivia", this)
                }
            }

            if (child.tag().toString() == "h2") { // нашли заголовок секции
                when (child.child(0).id()) {
                    "Trivia", ".D0.A4.D0.B0.D0.BA.D1.82.D1.8B" -> {
                        nextSectionIsTips = true
                    }
                }
            }
        }
    }

    private fun JSONObject.loadAbilities(root: Document) {
        val spells = root.getElementsByAttributeValue("style", "display: flex; flex-wrap: wrap; align-items: flex-start;")
        JSONArray().apply {
            spells.forEach {
                JSONObject().apply {
                    val spellName = it.getElementsByTag("div")[3].childNode(0).toString().trim()
                    put("spellName", spellName)

                    val spellAudio = it.getElementsByTag("source").attr("src")
                    put("spellAudio", spellAudio)

                    val hotKey = it.getElementsByAttributeValue("title", "Hotkey").getOrNull(0)?.text() ?: ""
                    put("hotKey", hotKey)

                    val legacyKey = it.getElementsByAttributeValue("title", "Legacy Key").getOrNull(0)?.text() ?: ""
                    put("legacyKey", legacyKey)

                    val effects = it.getElementsByAttributeValue("style", "display: inline-block; width: 32%; vertical-align: top;")
                    JSONArray().apply {
                        effects.mapNotNull { if (it.text() != "") add(it.text().replace("(", "(TagAghanim")) }
                        put("effects", this)
                    }

                    val description = it.getElementsByTag("div")[14].text()
                    put("description", description)

                    val params = it.getElementsByAttributeValue("style", "vertical-align:top; padding: 3px 5px; display:inline-block;")[0].children()
                    params.filter { it.attr("style").isEmpty() }.also {
                        JSONArray().apply {
                            it.forEach {
                                if (it.getElementsByAttribute("href").getOrNull(0)?.attr("href").equals("/Aghanim%27s_Scepter")) {
                                    add(it.text().replace("(", "(TagAghanim"))
                                } else {
                                    add(it.text().replace("(", "(TagTalent"))
                                }
                            }
                            put("params", this)
                        }
                    }

                    val cooldown = it.getElementsByAttributeValue("style", "display:inline-block; margin:8px 0px 0px 50px; width:190px; vertical-align:top;").getOrNull(0)
                    if (cooldown?.getElementsByAttribute("href")?.getOrNull(0)?.attr("href").equals("/Aghanim%27s_Scepter")) {
                        put("cooldown", cooldown?.text()?.replace("(", "(TagAghanim"))
                    } else {
                        put("cooldown", cooldown?.text()?.replace("(", "(TagTalent"))
                    }

                    val mana = it.getElementsByAttributeValue("style", "display:inline-block; margin:8px 0px 0px; width:190px; vertical-align:top;").getOrNull(0)
                    if (mana?.getElementsByAttribute("href")?.getOrNull(0)?.attr("href").equals("/Aghanim%27s_Scepter")) {
                        put("mana", mana?.text()?.replace("(", "(TagAghanim"))
                    } else {
                        put("mana", mana?.text()?.replace("(", "(TagTalent"))
                    }

                    val itemBehaviour = it.getElementsByAttributeValue("style", "margin-left: 50px;")
                    JSONArray().apply {
                        itemBehaviour.forEach {
                            val alt = it.getElementsByTag("img").attr("src")
                            ifContainAdd(alt, "Spell_immunity_block_partial_symbol.png", it)
                            ifContainAdd(alt, "Spell_block_partial_symbol.png", it)
                            ifContainAdd(alt, "Spell_immunity_block_symbol.png", it)
                            ifContainAdd(alt, "Disjointable_symbol.png", it)
                            ifContainAdd(alt, "Aghanim%27s_Scepter_symbol.png", it)
                            ifContainAdd(alt, "Breakable_symbol.png", it)
                            ifContainAdd(alt, "Breakable_partial_symbol.png", it)
                        }

                        put("itemBehaviour", this)
                    }

                    val story = it.getElementsByAttributeValue("style", "margin-top: 5px; padding-top: 2px; border-top: 1px solid #C1C1C1;").getOrNull(0)
                    put("story", story?.text())

                    val notesBlock = it.getElementsByAttributeValue("style", "flex: 1 1 450px; word-wrap: break-word;").getOrNull(0)
                    val notes = notesBlock?.getElementsByTag("li")
                    JSONArray().apply {
                        notes?.forEach {
                            add(it.text().replace("( ", "(TagTalent "))
                        }

                        put("notes", this)
                    }



                    add(this)
                }
            }
            put("abilities", this)
        }
    }

    private fun JSONArray.ifContainAdd(alt: String, spellImmunityBlockPartial: String, it: Element) {
        if (alt.contains(spellImmunityBlockPartial)) {
            add("($spellImmunityBlockPartial)^" + it.text().replace(". ", ".\n"))
        }
    }

    private fun JSONObject.loadHistory(root: Document) {
        val history = root.getElementsByAttributeValue("style", "display: table-cell; font-style: italic;")[0].text()
        put("history", history)
    }

    private fun JSONObject.loadDescription(root: Document) {
        val description = root.getElementsByTag("p")[0].text()
        put("description", description)
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


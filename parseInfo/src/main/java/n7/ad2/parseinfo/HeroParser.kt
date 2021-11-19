@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.parseinfo

import kotlinx.coroutines.runBlocking
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

data class HeroAndUrl(val name: String, val url: String)

class HeroParser {

    companion object {
        private const val SPELL_FOLDER = "heroesSpell"
        private const val HEROES_FOLDER = "heroes"
        private const val FILE_NAME = "heroes.json"
        private const val MAIN_URL_RU = "https://dota2.fandom.com/ru/wiki"
        private const val MAIN_URL_EN = "https://dota2.fandom.com/wiki"
        private const val HEROES_URL_RU = "$MAIN_URL_RU/Heroes"
        private const val HEROES_URL_EN = "$MAIN_URL_EN/Heroes"
    }

    enum class LOCALE(val heroesUrl: String, val baseUrl: String, val directory: String, val response: String) {
        RU("https://dota2.fandom.com/ru/wiki/%D0%93%D0%B5%D1%80%D0%BE%D0%B8", "https://dota2.fandom.com/ru/wiki/", "ru", "Реплики"),
        EN("https://dota2.fandom.com/wiki/Heroes", "https://dota2.fandom.com/wiki/", "en", "Responses")
    }

    fun loadHeroesOnEnglish() {
        getHeroesList(HEROES_URL_EN, MAIN_URL_EN).forEach(::loadHero)
    }

    fun loadHeroesOnRussian() {
//        val list = getHeroesList(HEROES_URL_RU)
    }

    fun loadResponsesOnEnglish() {
//        loadResponses(LOCALE.EN, list)
    }

    fun loadResponsesOnRussian() {
//        loadResponses(LOCALE.RU, list)
    }

    private fun loadResponses(locale: LOCALE, heroList: ArrayList<String>) {
        heroList
//            .filter { it == "Dawnbreaker" }
            .forEach { hero ->
                val root = connectTo("${locale.baseUrl}${hero}/${locale.response}")
                val allResponsesWithCategories = JSONArray()

                JSONArray().apply {
                    var count = 0
                    val children = root.getElementsByAttributeValue("class", "mw-parser-output")[0].children()
                    var category = JSONObject()
                    var responses = JSONArray()
                    var response: JSONObject

                    for (child in children) {
                        if (child.tag().toString() == "h2") {
                            if (category.size != 0) allResponsesWithCategories.add(category)
                            category = JSONObject()
                            responses = JSONArray()

                            count++
                            if (child.children().size > 1) {
                                category["category"] = child.child(1).text().trim()
                            } else {
                                category["category"] = child.child(0).text().trim()
                            }
                        }
                        if (child.tag().toString() == "ul") {
//                        if(child.child(0).children().size == 0) continue // реплики без URL
//                        if(child.children().size >1) // cекция без реплик

                            child.children().forEach node@{ node ->
                                response = JSONObject()
                                val audioUrl = node.getElementsByTag("a").getOrNull(0) ?: return@node
                                val audioUrl2 = node.getElementsByTag("a").getOrNull(2)?.attr("href")?.toString()
                                response["audioUrl"] = audioUrl.attr("href").toString()
                                response["title"] = node.let { innerNode ->
                                    innerNode.getElementsByTag("span").forEach { span -> span.remove() }
                                    innerNode.text()
                                }
                                if (node.getElementsByTag("img").size > 0) {
                                    response["icons"] = JSONArray().apply {
                                        node.let { innerNode ->
                                            innerNode.getElementsByTag("span").forEach { span -> span.remove() }
                                            innerNode.getElementsByTag("a").forEach { image ->
                                                val regex = Regex(" \\(.+?\\)")
                                                var title = image.attr("title")
                                                val matches = regex.containsMatchIn(title)
                                                if (matches) {
                                                    title = "items/" + title.replace(regex, "") + "/full.webp"
                                                    add(title)
                                                } else {
                                                    title = "heroes/$title/minimap.png"
                                                    add(title)
                                                }
                                            }
                                        }
                                    }
                                }
                                val previousTitle = (responses.getOrNull(responses.size - 1) as? JSONObject)?.getOrDefault("title", "-")
                                if (previousTitle == response["title"]) response["isArcane"] = true
                                responses.add(response)

                                if (audioUrl2 != null && !audioUrl2.startsWith("/")) {
                                    val oldCopy = response
                                    response = JSONObject()
                                    if (oldCopy.containsKey("icons")) response["icons"] = oldCopy.get("icons")
                                    response["audioUrl"] = audioUrl2
                                    response["title"] = oldCopy["title"]
                                    response["isArcane"] = true
                                    responses.add(response)
                                }
                            }
                            category["responses"] = responses
                        }
                    }
                    allResponsesWithCategories.add(category)
                }

                println("response in ${locale.directory} for hero $hero saved (${allResponsesWithCategories.toString().length} bytes)")
                File(assets + File.separator + HEROES_FOLDER + File.separator + hero + File.separator + locale.directory + File.separator + "responses.json").writeText(
                    allResponsesWithCategories.toString())
            }
    }

    private fun getHeroesElements(document: Document): Elements {
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

    fun createFileWithHeroesAndFolders() {
        val path = assets + FILE_NAME
        val root = connectTo(HEROES_URL_EN)
        var mainAttribute = "Strength"

        JSONObject().apply {
            JSONArray().apply {
                val heroesEng = getHeroesElements(root)
                heroesEng.forEachIndexed { index, _ ->
                    val heroObject = JSONObject().apply {
                        val heroName = getHeroName(heroesEng[index])
                        put("name", heroName)
                        if (heroName == "Anti-Mage") mainAttribute = "Agility"
                        if (heroName == "Ancient Apparition") mainAttribute = "Intelligence"
                        put("mainAttribute", mainAttribute)
                        val url = getHeroHref(heroesEng[index])
                        put("url", url)
                        val directory = "$HEROES_FOLDER/$heroName"
                        File("$assets$directory/en").mkdirs()
                        File("$assets$directory/ru").mkdirs()
                    }
                    add(heroObject)
                }
                put("heroes", this)
            }
            File(path).writeText(toJSONString())
        }
        println("heroes loaded $path")
    }

    private fun getHeroesList(heroesUrl: String, mainUrl: String): List<HeroAndUrl> {
        val root = connectTo(heroesUrl)
        return getHeroesElements(root).map { element ->
            val name = getHeroName(element)
            HeroAndUrl(name, "$mainUrl/$name")
        }.toList()
    }

    private fun loadHero(heroAndUrl: HeroAndUrl) {
        val (name, heroUrl) = heroAndUrl
        val root = connectTo(heroUrl)
        loadHeroImageFull(root, name)
        loadHeroImageMinimap(root, name)
//        loadHeroInformation(root, heroLocalizedDirectory)
    }

    private fun loadHeroInformation(root: Document, directory: String) {
        JSONObject().apply {
            loadDescription(root)
            loadHistory(root)
            loadAbilities(root)
            loadTrivia(root)
            loadTalents(root)
            loadMainAttributes(root)

            File(assets + directory + File.separator + "description.json").writeText(toJSONString())
        }
    }

    private fun JSONObject.loadMainAttributes(root: Document) {
        val mainAttributes = root.getElementsByAttributeValue("style", "width: 100%; padding: 4px 0; display: grid; grid-template-columns: auto auto auto; color: white; text-align: center;")[0]
        val mainAttributesElements = mainAttributes.getElementsByTag("div")

        var index = 4
        if (mainAttributesElements.size > 7) index = 7
        val attrStrength = (mainAttributesElements[index].childNode(0) as TextNode).text().split(" ").first().toDouble()
        val attrStrengthInc = (mainAttributesElements[index].childNode(0) as TextNode).text().split(" ").last().replace(",", ".").toDouble()
        val attrAgility = (mainAttributesElements[index + 1].childNode(0) as TextNode).text().split(" ").first().toDouble()
        val attrAgilityInc = (mainAttributesElements[index + 1].childNode(0) as TextNode).text().split(" ").last().replace(",", ".").toDouble()
        val attrIntelligence = (mainAttributesElements[index + 2].childNode(0) as TextNode).text().split(" ").first().toDouble()
        val attrIntelligenceInc = (mainAttributesElements[index + 2].childNode(0) as TextNode).text().split(" ").last().replace(",", ".").toDouble()

        val attrs = JSONObject().apply {
            put("attrStrength", attrStrength)
            put("attrStrengthInc", attrStrengthInc)
            put("attrAgility", attrAgility)
            put("attrAgilityInc", attrAgilityInc)
            put("attrIntelligence", attrIntelligence)
            put("attrIntelligenceInc", attrIntelligenceInc)
        }
        put("mainAttributes", attrs)
    }

    private fun JSONObject.loadTalents(root: Document) {
        val talentBlock = root.getElementsByAttributeValue("style", "display:flex; flex-wrap:wrap; align-items:flex-start;")[0]
        val talentLines = talentBlock.getElementsByTag("tr")
        val abilities: JSONArray = get("abilities") as JSONArray
        val talents = JSONArray().apply {
            var talentLvl = 25
            for (talentLine in talentLines) {
                if (talentLine.children().size == 1) continue
                JSONObject().apply {
                    put("talentLeft", talentLine.child(0).text())
                    put("talentLvl", talentLvl.toString())
                    put("talentRight", talentLine.child(2).text())
                    add(this)
                }
                talentLvl -= 5
            }

            put("talents", this)
        }

        val notes = JSONArray().apply {
            val talentTips = talentBlock.getElementsByTag("li")
            for (talentTip in talentTips) {
                add(talentTip.text())
            }
        }
        val abilityTalent = JSONObject().apply {
            put("spellName", "Talent")
            if (notes.size > 0) put("notes", notes)
            put("talents", talents)
        }
        abilities.add(0, abilityTalent)
        put("abilities", abilities)
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

//                    if (loadHeroSpellImage) loadSpellImage(it, spellName)

                    var audioUrl = it.getElementsByTag("source").attr("src")
                    if (audioUrl.isNullOrEmpty()) audioUrl = null
                    put("audioUrl", audioUrl)

                    val hotKey = it.getElementsByAttributeValue("class", "tooltip").getOrNull(0)?.text()?.takeIf { it.length == 1 }
                    put("hotKey", hotKey)

                    val legacyKey = it.getElementsByAttributeValue("class", "tooltip").getOrNull(1)?.text()?.takeIf { it.length == 1 }
                    put("legacyKey", legacyKey)

                    val effects = it.getElementsByAttributeValue("style", "display: inline-block; width: 32%; vertical-align: top;")
                    JSONArray().apply {
                        effects.mapNotNull { if (it.text() != "") add(it.text().replace("(", "(TagAghanim")) }
                        put("effects", this)
                    }

                    val description = it.getElementsByTag("div")[12].text()
                    put("description", description)

                    val params = it.getElementsByAttributeValue("style", "vertical-align:top; padding: 3px 5px; display:inline-block;")[0].children()
                    params.filter { it.attr("style").isEmpty() && !it.attr("class").equals("ability-lore") }.also {
                        JSONArray().apply {
                            it.forEach {
                                if (it.getElementsByAttribute("href").getOrNull(0)?.attr("href")?.endsWith("/Aghanim%27s_Scepter") ?: false) {
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
                        // todo add AGHANIM SHARD ебучий
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

                    val story = it.getElementsByAttributeValue("style", "margin-top: 5px; padding: 2px 10px 5px;text-align:center").getOrNull(0)
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

    private fun loadSpellImage(it: Element, spellName: String) {
        try {
            val spellImage = it.getElementsByAttributeValue("class", "image")[0].attr("href")
            saveImageInDirectory(spellImage, SPELL_FOLDER + File.separator, "$spellName.png")
        } catch (e: Exception) {
            println("cannot download hero spell $spellName")
        }
    }

    private fun JSONArray.ifContainAdd(alt: String, spellImmunityBlockPartial: String, it: Element) {
        if (alt.contains(spellImmunityBlockPartial)) {
            add("(${spellImmunityBlockPartial.dropLast(4)})^"
                    + it.getElementsByAttribute("title")[0].attr("title").dropLast(1) + "\n"
                    + it.text().replace(". ", "\n"))
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

    private fun loadHeroImageFull(root: Document, name: String) {
        val url = root.getElementsByTag("img")[2].attr("data-src")
        saveImageInDirectory(url, "$HEROES_FOLDER/$name", "full.png")
    }

    private fun loadHeroImageMinimap(root: Document, name: String) {
        val url = root.getElementsByTag("img")[21].attr("data-src")
        saveImageInDirectory(url, "$HEROES_FOLDER/$name", "minimap.png")
    }

    private fun saveImageInDirectory(url: String, directory: String, name: String) {
        val path = assets + directory + File.separator + name
        try {
            val file = File(path)
            if (file.exists()) return
            file.mkdirs()
            val bufferImageIO = ImageIO.read(URL(url))
            ImageIO.write(bufferImageIO, "png", file)
            println("image $path saved")
        } catch (e: Exception) {
            println("image $path not saved")
        }
    }

}

fun main() = runBlocking {
    val heroParser = HeroParser()
//    heroParser.createFileWithHeroesAndFolders()
    heroParser.loadHeroesOnEnglish()
}


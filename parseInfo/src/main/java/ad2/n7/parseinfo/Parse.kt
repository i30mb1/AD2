package ad2.n7.parseinfo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.Jsoup
import java.io.File

class Parse : CoroutineScope by (CoroutineScope(Dispatchers.IO)) {

    fun loadHeroes() {
        val heroesEngUrl = "https://dota2.gamepedia.com/Heroes"
        val fileName = "heroesNew.json"
        val filePath = System.getProperty("user.dir") + "\\app\\src\\main\\assets\\"

        val root = Jsoup.connect(heroesEngUrl).get()

        val jsonHeroes = JSONObject().apply {
            JSONArray().apply {
                val heroesTable = root.getElementsByAttributeValue("style", "text-align:center")[0]
                val heroes = heroesTable.getElementsByAttributeValue("style", "width:150px; height:84px; display:inline-block; overflow:hidden; margin:1px")

                heroes.forEach {
                    val heroObject = JSONObject().apply {
                        val heroName = it.getElementsByTag("a")[0].attr("title")
                        val heroHref = it.getElementsByTag("a")[0].attr("href")

                        put("name", heroName)
                        put("href", heroHref)
                    }
                    add(heroObject)
                }
                put("heroes", this)
            }
        }

        File(filePath + fileName).writeText(jsonHeroes.toJSONString())
    }

}

fun main() = runBlocking {
    val parse = Parse()
    parse.loadHeroes()
}


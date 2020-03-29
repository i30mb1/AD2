package ad2.n7.parseinfo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.io.File

class Parse : CoroutineScope by (CoroutineScope(Dispatchers.IO)) {

    fun loadHeroes() {
        val heroesEngUrl = "https://dota2.gamepedia.com/Heroes"
        val fileName = "heroesNew.json"
        val filePath = System.getProperty("user.dir") + "\\app\\src\\main\\assets\\"

        val jsonHeroes = JSONObject().apply {
            val jsonHeroesArray = JSONArray()
            put("heroes", jsonHeroesArray)
        }

        File(filePath + fileName).writeText(jsonHeroes.toJSONString())
    }

}

fun main() = runBlocking {
    val parse = Parse()
    parse.loadHeroes()
}


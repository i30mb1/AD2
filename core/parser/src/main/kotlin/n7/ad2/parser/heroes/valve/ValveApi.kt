package n7.ad2.parser.heroes.valve

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import n7.ad2.parser.LocaleHeroes
import n7.ad2.parser.assetsDatabase
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

/** Official Dota 2 datafeed, the same data dota2.com/heroes is built from. */
internal object ValveApi {

    private const val BASE_URL = "https://www.dota2.com/datafeed"
    private const val ABILITY_IMAGE_URL = "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/abilities/%s.png"
    private const val INNATE_IMAGE_URL = "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/icons/innate_icon.png"
    const val INNATE_IMAGE_NAME = "innate_icon"

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    internal class HeroListItem(val id: Long, val name: String) {
        val folderName: String = name.replace(" ", "_").lowercase()
    }

    private val LocaleHeroes.language: String
        get() = when (this) {
            LocaleHeroes.RU -> "russian"
            LocaleHeroes.EN -> "english"
        }

    fun heroList(): List<HeroListItem> {
        val heroes = getJson("$BASE_URL/herolist?language=english").path("result", "data", "heroes") as JSONArray
        return heroes.filterIsInstance<JSONObject>()
            .map { HeroListItem((it["id"] as Number).toLong(), it["name_english_loc"] as String) }
            .sortedBy { it.name }
    }

    fun heroData(id: Long, locale: LocaleHeroes): JSONObject {
        val heroes = getJson("$BASE_URL/herodata?language=${locale.language}&hero_id=$id").path("result", "data", "heroes") as JSONArray
        return heroes.firstOrNull() as? JSONObject ?: error("no data for hero $id")
    }

    fun saveAbilityImage(abilityKey: String) = saveImage(ABILITY_IMAGE_URL.format(abilityKey), abilityKey)

    /** Innate abilities without an own icon use this one, as the game does. */
    fun saveInnateImage() = saveImage(INNATE_IMAGE_URL, INNATE_IMAGE_NAME)

    class Patch(val name: String, val timestamp: Long)

    /** Newest first. */
    val patches: List<Patch> by lazy {
        val list = getJson("$BASE_URL/patchnoteslist?language=english")["patches"] as JSONArray
        list.filterIsInstance<JSONObject>()
            .map { Patch(it["patch_number"] as String, (it["patch_timestamp"] as Number).toLong()) }
            .sortedByDescending { it.timestamp }
    }

    private val patchNotesCache = mutableMapOf<Pair<String, LocaleHeroes>, JSONObject>()

    fun patchNotes(patch: String, locale: LocaleHeroes): JSONObject = patchNotesCache.getOrPut(patch to locale) {
        getJson("$BASE_URL/patchnotes?version=$patch&language=${locale.language}")
    }

    /** Downloads the icon once and stores it as webp (ffmpeg) to keep the assets small, png otherwise. */
    private fun saveImage(url: String, name: String) {
        val directory = File(assetsDatabase, "images/abilities").apply { mkdirs() }
        val webp = File(directory, "$name.webp")
        if (webp.exists() && webp.length() > 0) return

        val request = HttpRequest.newBuilder(URI(url)).timeout(Duration.ofSeconds(30)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        if (response.statusCode() != 200) error("HTTP ${response.statusCode()}")
        val png = File(directory, "$name.png").apply { writeBytes(response.body()) }

        val converted = try {
            val process = ProcessBuilder("ffmpeg", "-y", "-loglevel", "error", "-i", png.path, "-c:v", "libwebp", "-quality", "90", webp.path)
                .redirectErrorStream(true)
                .start()
            process.inputStream.readAllBytes()
            process.waitFor() == 0 && webp.length() > 0
        } catch (e: Exception) {
            false
        }
        if (converted) {
            png.delete()
        } else {
            webp.delete()
            png.renameTo(webp) // Android decodes by content, not by extension
            println("  [image] ffmpeg is not available, $name stored as png data")
        }
    }

    private fun getJson(url: String): JSONObject {
        val request = HttpRequest.newBuilder(URI(url))
            .timeout(Duration.ofSeconds(30))
            .header("Accept", "application/json")
            .build()
        var lastError: Exception? = null
        repeat(3) { attempt ->
            try {
                val response = client.send(request, HttpResponse.BodyHandlers.ofString(Charsets.UTF_8))
                if (response.statusCode() == 200) return JSONParser().parse(response.body()) as JSONObject
                lastError = IllegalStateException("HTTP ${response.statusCode()} for $url")
            } catch (e: Exception) {
                lastError = e
            }
            Thread.sleep(1000L * (attempt + 1))
        }
        throw lastError ?: IllegalStateException("failed $url")
    }

    private fun JSONObject.path(vararg keys: String): Any? = keys.fold(this as Any?) { node, key -> (node as? JSONObject)?.get(key) }
}

package n7.ad2.items.domain.internal.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.AppInformation
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.items.domain.model.News
import n7.ad2.items.domain.usecase.GetNewsUseCase
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Loads Dota 2 news from the public Steam News API (appid 570) as JSON.
 *
 * The previous implementation scraped dotabuff.com/blog, but dotabuff is now Cloudflare bot-protected
 * (HTTP 403) so the scrape returned nothing and the feed was always empty. The Steam News API needs no
 * scraping and is locale-independent. Article images aren't a first-class field, so we pull the first
 * image URL out of the post body (expanding Steam's {STEAM_CLAN_IMAGE} token) and fall back to the Dota 2
 * header capsule when a post has none.
 */
internal class GetNewsUseCaseImpl constructor(private val logger: Logger, private val appInformation: AppInformation, private val dispatchers: DispatchersProvider) : GetNewsUseCase {

    override suspend operator fun invoke(page: Int): Flow<List<News>> = flow {
        val body = httpGet("$NEWS_URL&count=${page * PAGE_SIZE}")
        val items = JSONObject(body).getJSONObject("appnews").getJSONArray("newsitems")
        val result = (0 until items.length()).map(items::getJSONObject)
            .drop((page - 1) * PAGE_SIZE)
            .take(PAGE_SIZE)
            .map { item ->
                News(
                    title = item.optString("title"),
                    urlImage = imageFrom(item.optString("contents")),
                    loadedFromPage = page,
                    articleUrl = item.optString("url"),
                )
            }
        emit(result)
    }.catch {
        logger.log("could not load news page $page: ${it.message}")
        emit(emptyList())
    }.flowOn(dispatchers.IO)

    private fun httpGet(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "AD2/1.0")
        }
        return try {
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun imageFrom(contents: String): String {
        val expanded = contents.replace("{STEAM_CLAN_IMAGE}", STEAM_CLAN_IMAGE)
        return IMAGE_REGEX.find(expanded)?.value ?: DOTA_HEADER_IMAGE
    }

    private companion object {
        const val NEWS_URL = "https://api.steampowered.com/ISteamNews/GetNewsForApp/v2/?appid=570"
        const val PAGE_SIZE = 15
        const val CONNECT_TIMEOUT_MS = 10_000
        const val READ_TIMEOUT_MS = 20_000
        const val STEAM_CLAN_IMAGE = "https://clan.cloudflare.steamstatic.com/images"
        const val DOTA_HEADER_IMAGE = "https://cdn.cloudflare.steamstatic.com/steam/apps/570/header.jpg"
        val IMAGE_REGEX = Regex("""https?://[^\s\]"']+\.(?:jpg|jpeg|png|gif)""", RegexOption.IGNORE_CASE)
    }
}

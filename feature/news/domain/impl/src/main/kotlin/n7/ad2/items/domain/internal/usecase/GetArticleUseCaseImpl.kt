package n7.ad2.items.domain.internal.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.api.dao.NewsDao
import n7.ad2.items.domain.model.Article
import n7.ad2.items.domain.usecase.GetArticleUseCase
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL

/**
 * The feed (see [GetNewsUseCaseImpl]) comes from the Steam News API, so the article body comes from the same API:
 * the old implementation scraped the dotabuff page behind `href`, which is Cloudflare protected now.
 * Steam announcements (`feed_type` 1) are BBCode, posts of external feeds (`feed_type` 0) are plain HTML.
 */
internal class GetArticleUseCaseImpl constructor(private val newsDao: NewsDao, private val dispatchers: DispatchersProvider) : GetArticleUseCase {

    override operator fun invoke(newsID: Int): Flow<Article> = flow {
        val news = newsDao.getSingleNews(newsID)
        val items = JSONObject(httpGet(NEWS_URL)).getJSONObject("appnews").getJSONArray("newsitems")
        val item = (0 until items.length()).map(items::getJSONObject).firstOrNull { it.optString("url") == news.href }
            ?: error("article ${news.href} is not in the feed anymore")
        val contents = item.optString("contents").replace(CLAN_IMAGE_TOKEN, STEAM_CLAN_IMAGE)
        val html = if (item.optInt("feed_type") == 1) bbCodeToHtml(contents) else cleanHtml(contents)
        emit(Article(ArticleHtmlParser.parse(html)))
    }.flowOn(dispatchers.IO)

    private fun httpGet(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 20_000
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

    private fun cleanHtml(html: String): String {
        val body = Jsoup.parseBodyFragment(html)
        body.getElementsByTag("iframe").remove()
        return body.body().html()
    }

    private fun bbCodeToHtml(bbCode: String): String {
        var text = bbCode.replace("\r", "")
        // new lines next to block tags are only formatting, the rest are real line breaks
        text = BLOCK_NEWLINES.replace(text) { it.groupValues[1] }
        text = text.replace("\n", "<br>")
        text = IMG_WITH_SRC.replace(text) { """<img src="${it.groupValues[1]}"/>""" }
        text = IMG_WITH_BODY.replace(text) { """<img src="${it.groupValues[1].trim()}"/>""" }
        // links stay links, the screen opens them in the browser
        text = URL_TAG.replace(text) { match ->
            val href = match.groupValues[1].trim().trim('"').ifEmpty { match.groupValues[2] }
            """<a href="$href">${match.groupValues[2]}</a>"""
        }
        text = EMPTY_TAG.replace(text, "")
        for ((bb, html) in SIMPLE_TAGS) {
            text = text.replace("[$bb]", "<$html>", ignoreCase = true)
            text = text.replace("[/$bb]", "</$html>", ignoreCase = true)
        }
        text = text.replace("[*]", "<li>")
        return UNKNOWN_TAG.replace(text, "")
    }

    private companion object {
        const val NEWS_URL = "https://api.steampowered.com/ISteamNews/GetNewsForApp/v2/?appid=570&count=300&maxlength=0"
        const val STEAM_CLAN_IMAGE = "https://clan.cloudflare.steamstatic.com/images"
        // Android's ICU regex is stricter than java.util: every ] and } is escaped
        val CLAN_IMAGE_TOKEN = Regex("""\{STEAM_CLAN_(?:LOC_)?IMAGE\}""")

        val BLOCK_NEWLINES = Regex("""\n*(\[/?(?:p|h[1-6]|list|olist|table|tr|td|th|expand[^\]]*)\]|\[\*\])\n*""", RegexOption.IGNORE_CASE)
        val IMG_WITH_SRC = Regex("""\[img src="([^"]+)"[^\]]*\]\s*\[/img\]""", RegexOption.IGNORE_CASE)
        val IMG_WITH_BODY = Regex("""\[img[^\]]*\](.*?)\[/img\]""", RegexOption.IGNORE_CASE)
        val URL_TAG = Regex("""\[url(?:=([^\]]*))?\](.*?)\[/url\]""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val EMPTY_TAG = Regex("""\[previewyoutube[^\]]*\]\[/previewyoutube\]""", RegexOption.IGNORE_CASE)
        val UNKNOWN_TAG = Regex("""\[/?(?:expand|previewyoutube|o|spoiler|noparse)[^\]]*\]""", RegexOption.IGNORE_CASE)
        val SIMPLE_TAGS = mapOf(
            "p" to "p", "h1" to "h1", "h2" to "h2", "h3" to "h3", "h4" to "h4", "h5" to "h5",
            "b" to "b", "i" to "i", "u" to "u", "strike" to "s", "quote" to "blockquote", "code" to "pre",
            "list" to "ul", "olist" to "ol", "table" to "table", "tr" to "tr", "td" to "td", "th" to "th",
        )
    }
}

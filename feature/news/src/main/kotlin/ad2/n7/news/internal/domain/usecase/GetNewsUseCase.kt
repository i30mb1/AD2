package ad2.n7.news.internal.domain.usecase

import ad2.n7.news.internal.domain.model.NewsVO
import kotlinx.coroutines.withContext
import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.coroutines.DispatchersProvider
import org.jsoup.Jsoup
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val appInformation: AppInformation,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(page: Int): List<NewsVO> = withContext(dispatchers.IO) {
        val baseUrl = when (appInformation.appLocale) {
            AppLocale.English -> "https://www.dotabuff.com/blog?page=$page"
            AppLocale.Russian -> "https://ru.dotabuff.com/blog?page=$page"
        }
        val doc = Jsoup.connect(baseUrl).get()
        val body = doc.getElementsByClass("related-posts")
        val news = body[0].getElementsByTag("a")
        buildList {
            news.forEach { element ->
                val href = element.attr("href")
                val title = element.getElementsByClass("headline").getOrNull(0)?.text()
                val style = element.childNode(0).attr("style")
                val imageUrl = style.substringAfter("url(").substringBefore(")")
                if (title != null) add(NewsVO(title, imageUrl))
            }
        }
    }


}
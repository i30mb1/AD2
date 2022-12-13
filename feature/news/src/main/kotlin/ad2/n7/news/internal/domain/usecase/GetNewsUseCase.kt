package ad2.n7.news.internal.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.internal.model.NewsLocal
import n7.ad2.logger.Logger
import org.jsoup.Jsoup
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val logger: Logger,
    private val appInformation: AppInformation,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(page: Int): Flow<List<NewsLocal>> = flow {
        val baseUrl = when (appInformation.appLocale) {
            AppLocale.English -> "https://www.dotabuff.com/blog"
            AppLocale.Russian -> "https://ru.dotabuff.com/blog"
        }
        val doc = Jsoup.connect("$baseUrl?page=$page").get()
        val body = doc.getElementsByClass("related-posts")
        val news = body[0].getElementsByTag("a")
        val result = buildList {
            news.forEach { element ->
                val href = element.attr("href")
                val title = element.getElementsByClass("headline").getOrNull(0)?.text()
                val style = element.childNode(0).attr("style")
                val imageUrl = style.substringAfter("url(").substringBefore(")")
                if (title != null) add(NewsLocal(
                    title = "/$href",
                    urlImage = imageUrl,
                    loadedFromPage = page,
                    href = href,
                ))
            }
        }
        emit(result)
    }
        .catch {
            logger.log("could not loading news page $page")
            emit(emptyList())
        }
        .flowOn(dispatchers.IO)


}
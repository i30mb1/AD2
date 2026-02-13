package n7.ad2.items.domain.internal.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.items.domain.model.News
import n7.ad2.items.domain.usecase.GetNewsUseCase
import org.jsoup.Jsoup

internal class GetNewsUseCaseImpl constructor(private val logger: Logger, private val appInformation: AppInformation, private val dispatchers: DispatchersProvider) : GetNewsUseCase {

    override suspend operator fun invoke(page: Int): Flow<List<News>> = flow {
        val baseUrl = when (appInformation.appLocale) {
            AppLocale.English -> "https://www.dotabuff.com"
            AppLocale.Russian -> "https://ru.dotabuff.com"
        }
        val doc = Jsoup.connect("$baseUrl/blog?page=$page").get()
        val body = doc.getElementsByClass("related-posts")
        val news = body[0].getElementsByTag("a")
        val result = buildList {
            news.forEach { element ->
                val href = element.attr("href")
                val title = element.getElementsByClass("headline").getOrNull(0)?.text()
                val style = element.childNode(0).attr("style")
                val imageUrl = style.substringAfter("url(").substringBefore(")")
                if (title != null) {
                    add(
                        News(
                            title = title,
                            urlImage = imageUrl,
                            loadedFromPage = page,
                            articleUrl = "$baseUrl$href",
                        ),
                    )
                }
            }
        }
        emit(result)
    }.catch {
        logger.log("could not loading news page $page")
        emit(emptyList())
    }.flowOn(dispatchers.IO)
}

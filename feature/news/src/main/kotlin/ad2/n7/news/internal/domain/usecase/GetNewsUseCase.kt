package ad2.n7.news.internal.domain.usecase

import ad2.n7.news.internal.domain.model.NewsVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.coroutines.DispatchersProvider
import org.jsoup.Jsoup
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val appInformation: AppInformation,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(): Flow<List<NewsVO>> = flow {
        val baseUrl = when (appInformation.appLocale) {
            AppLocale.English -> "https://www.dotabuff.com/blog?page="
            AppLocale.Russian -> "https://ru.dotabuff.com/blog?page="
        }
        val doc = Jsoup.connect(baseUrl).get()
        val body = doc.getElementsByClass("related-posts")
        val news = body[0].getElementsByTag("a")
        val result = buildList {
            news.forEach { element ->
                val href = element.attr("href")
                val title = element.getElementsByClass("headline").getOrNull(0)?.text()
                if (title != null) add(NewsVO(title))
            }
        }
        emit(result)
    }
        .flowOn(dispatchers.IO)


}
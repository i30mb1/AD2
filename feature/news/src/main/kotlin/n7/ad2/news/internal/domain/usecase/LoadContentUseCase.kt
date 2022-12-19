package n7.ad2.news.internal.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.api.dao.NewsDao
import org.jsoup.Jsoup
import javax.inject.Inject

internal class LoadContentUseCase @Inject constructor(
    private val newsDao: NewsDao,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(newsID: Int): Flow<String> = flow {
        val news = newsDao.getSingleNews(newsID)
        val doc = Jsoup.connect(news.href).get()
        doc.getElementsByTag("img").forEach { image ->
            if (image.attr("src").startsWith("/assets/")) image.remove()
        }
        for (a in doc.getElementsByTag("a")) a.removeAttr("href")
        for (iframe in doc.getElementsByTag("iframe")) iframe.remove()
        for (social in doc.getElementsByClass("social")) social.remove()
        for (element in doc.getElementsByClass("categories categories-bottom")) element.remove()
        val body = if (doc.getElementsByClass("story-big").size == 0) doc.getElementsByClass("body")
        else doc.getElementsByClass("story-big")
        emit(body.toString())
    }
        .flowOn(dispatchers.Default)

}
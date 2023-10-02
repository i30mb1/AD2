package n7.ad2.news.ui.internal.screen.news

import n7.ad2.database_guides.internal.model.NewsDB
import n7.ad2.items.domain.model.News

internal object NewsToNewsDBMapper : (News) -> NewsDB {

    override operator fun invoke(from: News) = NewsDB(
        title = from.title,
        urlImage = from.urlImage,
        loadedFromPage = from.loadedFromPage,
        href = from.articleUrl
    )
}

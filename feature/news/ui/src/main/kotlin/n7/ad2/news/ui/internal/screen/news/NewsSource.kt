package n7.ad2.news.ui.internal.screen.news

import androidx.paging.PagingSource
import androidx.paging.PagingState
import n7.ad2.app.logger.Logger
import n7.ad2.database_guides.api.AppDatabase
import n7.ad2.database_guides.internal.model.NewsDB
import javax.inject.Inject

internal class NewsSource @Inject constructor(private val database: AppDatabase, private val logger: Logger) : PagingSource<Int, NewsDB>() {

    override fun getRefreshKey(state: PagingState<Int, NewsDB>): Int? {
        logger.log("refreshKey = ${state.anchorPosition}")
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsDB> = try {
        val page = params.key ?: 1
        logger.log("loading local news page=$page")
        val news = database.newsDao.getNews(page)
        logger.log("loading local news success=${news.size}")
        LoadResult.Page(
            news,
            if (page == 1) null else page - 1,
            if (news.isEmpty()) null else page + 1,
        )
    } catch (error: Exception) {
        logger.log("loading local news error=$error")
        LoadResult.Error(error)
    }
}

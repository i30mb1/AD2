package n7.ad2.news.internal.screen.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import javax.inject.Inject
import n7.ad2.app.logger.Logger
import n7.ad2.database_guides.api.AppDatabase
import n7.ad2.database_guides.internal.model.NewsLocal

internal class NewsSource @Inject constructor(
    private val database: AppDatabase,
    private val logger: Logger,
) : PagingSource<Int, NewsLocal>() {

    override fun getRefreshKey(state: PagingState<Int, NewsLocal>): Int? {
        logger.log("refreshKey = ${state.anchorPosition}")
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsLocal> = try {
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

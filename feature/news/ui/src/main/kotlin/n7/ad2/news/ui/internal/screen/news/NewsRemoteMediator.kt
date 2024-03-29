@file:OptIn(ExperimentalPagingApi::class)

package n7.ad2.news.ui.internal.screen.news

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import javax.inject.Inject
import kotlinx.coroutines.flow.single
import n7.ad2.app.logger.Logger
import n7.ad2.apppreference.Preference
import n7.ad2.database_guides.api.AppDatabase
import n7.ad2.database_guides.internal.model.NewsDB
import n7.ad2.items.domain.usecase.GetNewsUseCase

@OptIn(ExperimentalPagingApi::class)
internal class NewsRemoteMediator @Inject constructor(
    private val database: AppDatabase,
    private val preference: Preference,
    private val logger: Logger,
    private val getNewsUseCase: GetNewsUseCase,
) : RemoteMediator<Int, NewsDB>() {

    override suspend fun initialize(): InitializeAction {
        val needToUpdateNews = preference.isNeedToUpdateNews()
        logger.log("is need to update news = $needToUpdateNews")
        return if (needToUpdateNews) InitializeAction.LAUNCH_INITIAL_REFRESH
        else InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsDB>,
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) 1 else lastItem.loadedFromPage + 1
                }

                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(true)
            }
            logger.log("loading remote news page=$page")
            val response: List<NewsDB> = getNewsUseCase(page).single()
                .map(NewsToNewsDBMapper::invoke)
            database.withTransaction {
                if (loadType == LoadType.REFRESH) database.newsDao.clearAll()
                database.newsDao.insert(response)
                preference.saveUpdateNewsDate()
            }
            logger.log("loaded remote news=${response.size}")
            return MediatorResult.Success(response.isEmpty())
        } catch (error: Exception) {
            logger.log("loaded remote news error=$error")
            return MediatorResult.Error(error)
        }
    }

}

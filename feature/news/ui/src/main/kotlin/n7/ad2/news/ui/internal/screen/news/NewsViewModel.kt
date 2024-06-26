@file:OptIn(ExperimentalPagingApi::class, ExperimentalPagingApi::class)

package n7.ad2.news.ui.internal.screen.news

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import n7.ad2.database_guides.api.AppDatabase
import n7.ad2.news.ui.internal.screen.news.model.Image
import n7.ad2.news.ui.internal.screen.news.model.NewsVO

internal class NewsViewModel @AssistedInject constructor(
    private val database: AppDatabase,
    private val newsSource: NewsSource,
    newsRemoteMediator: NewsRemoteMediator,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): NewsViewModel
    }

    val newsState: MutableStateFlow<NewsState> = MutableStateFlow(NewsState.init())
    val news: Flow<PagingData<NewsVO>> = Pager(
        PagingConfig(pageSize = 10),
        null,
        newsRemoteMediator,
    ) { database.newsDao.getPagingSourceNews() }.flow.map { pagingData ->
        pagingData.map { newsLocal ->
            NewsVO(newsLocal.id, newsLocal.title, Image(newsLocal.urlImage))
        }
    }
}

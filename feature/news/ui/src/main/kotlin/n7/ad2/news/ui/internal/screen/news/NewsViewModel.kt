@file:OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)

package n7.ad2.news.ui.internal.screen.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import n7.ad2.database_guides.api.AppDatabase
import n7.ad2.news.ui.internal.common.formatShortNewsDate
import n7.ad2.news.ui.internal.screen.news.model.NewsFilter
import n7.ad2.news.ui.internal.screen.news.model.NewsVO

internal class NewsViewModel @AssistedInject constructor(private val database: AppDatabase, newsRemoteMediator: NewsRemoteMediator) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): NewsViewModel
    }

    private val _filter = MutableStateFlow(NewsFilter.ALL)
    val filter: StateFlow<NewsFilter> = _filter.asStateFlow()

    val news: Flow<PagingData<NewsVO>> = _filter
        .flatMapLatest { filter ->
            Pager(PagingConfig(pageSize = 10), null, newsRemoteMediator) {
                when (filter) {
                    NewsFilter.ALL -> database.newsDao.getPagingSourceNews()
                    NewsFilter.OFFICIAL -> database.newsDao.getPagingSourceNews(isOfficial = true)
                    NewsFilter.PRESS -> database.newsDao.getPagingSourceNews(isOfficial = false)
                }
            }.flow
        }
        .map { pagingData ->
            pagingData.map { news ->
                NewsVO(news.id, news.title, news.urlImage, news.source, formatShortNewsDate(news.date), news.isOfficial)
            }
        }
        .cachedIn(viewModelScope)

    fun setFilter(filter: NewsFilter) {
        _filter.value = filter
    }
}

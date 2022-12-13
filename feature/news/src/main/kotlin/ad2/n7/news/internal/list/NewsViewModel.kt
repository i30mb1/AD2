@file:OptIn(ExperimentalPagingApi::class, ExperimentalPagingApi::class)

package ad2.n7.news.internal.list

import ad2.n7.news.internal.domain.model.Image
import ad2.n7.news.internal.domain.model.NewsVO
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

internal class NewsViewModel @AssistedInject constructor(
    private val newsSource: NewsSource,
    newsRemoteMediator: NewsRemoteMediator,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): NewsViewModel
    }

    val state: MutableStateFlow<State> = MutableStateFlow(State.init())
    val news: Flow<PagingData<NewsVO>> = Pager(
        PagingConfig(pageSize = 10),
        1,
        newsRemoteMediator,
    ) { newsSource }.flow.map { pagingData ->
        pagingData.map { newsLocal ->
            NewsVO(newsLocal.id, newsLocal.title, Image(newsLocal.urlImage))
        }
    }


    data class State(
        val isLoading: Boolean,
        val isError: Boolean,
        val list: List<NewsVO>,
    ) {

        companion object {
            fun init() = State(
                isLoading = true,
                isError = false,
                list = emptyList()
            )
        }

    }

}
package ad2.n7.news.internal

import ad2.n7.news.internal.domain.model.NewsVO
import ad2.n7.news.internal.domain.usecase.GetNewsUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class NewsViewModel @AssistedInject constructor(
    private val newsSource: NewsSource,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): NewsViewModel
    }

    sealed class State {
        object Loading : State()
        data class Error(val error: String) : State()
        data class Data(val list: List<NewsVO>) : State()
    }

    val state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val news = Pager(PagingConfig(pageSize = 10), 1) { newsSource }
        .flow
        .cachedIn(viewModelScope)

    init {
        setupLiveDataNews()
    }

    private fun setupLiveDataNews() {

//        newsDao = NewsRoomDatabase.getDatabase(application).steamNewsDao()
//        val dataSource = newsDao.dataSourceNews
//        val config = PagedList.Config.Builder()
//            .setPageSize(12)
//            .setInitialLoadSizeHint(12)
//            .setPrefetchDistance(3)
//            .setEnablePlaceholders(true)
//            .build()
//        news = LivePagedListBuilder(dataSource, config).setBoundaryCallback(object : BoundaryCallback<NewsModel>() {
//            override fun onItemAtEndLoaded(itemAtEnd: NewsModel) {
//                super.onItemAtEndLoaded(itemAtEnd)
//                pageNews++
//                val data = Data.Builder().putInt(NewsWorker.PAGE, pageNews).build()
//                val worker = OneTimeWorkRequest.Builder(NewsWorker::class.java).setInputData(data).build()
//                WorkManager.getInstance(application).enqueue(worker)
//                WorkManager.getInstance(application).getWorkInfoByIdLiveData(worker.id).observeForever { workInfo ->
//                    if (workInfo != null) {
//                        if (workInfo.state.isFinished) {
////                            isLoading.set(false)
//                        } else {
////                            isLoading.set(true)
//                        }
//                    }
//                }
//            }
//        }).build()
    }

}

class NewsSource @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
) : PagingSource<Int, NewsVO>() {

    override fun getRefreshKey(state: PagingState<Int, NewsVO>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsVO> = try {
        val page = params.key ?: 1
        val news = getNewsUseCase(page)
        LoadResult.Page(
            news,
            if (page == 1) null else page - 1,
            if (news.isEmpty()) null else page + 1,
        )
    } catch (error: Exception) {
        LoadResult.Error(error)
    }

}
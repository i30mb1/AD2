@file:OptIn(ExperimentalPagingApi::class)

package ad2.n7.news.internal

import ad2.n7.news.internal.domain.model.Image
import ad2.n7.news.internal.domain.model.NewsVO
import ad2.n7.news.internal.domain.usecase.GetNewsUseCase
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.map
import androidx.room.withTransaction
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import n7.ad2.app_preference.Preference
import n7.ad2.database_guides.api.AppDatabase
import n7.ad2.database_guides.internal.model.NewsLocal
import javax.inject.Inject

internal class NewsViewModel @AssistedInject constructor(
    private val newsSource: NewsSource,
    private val newsRemoteMediator: NewsRemoteMediator,
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
    val news = Pager(PagingConfig(pageSize = 10), 1, newsRemoteMediator) { newsSource }
        .flow
        .map { pagingData ->
            pagingData.map { newsLocal ->
                NewsVO(newsLocal.title, Image(newsLocal.urlImage))
            }
        }

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
) : PagingSource<Int, NewsLocal>() {

    override fun getRefreshKey(state: PagingState<Int, NewsLocal>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsLocal> = try {
        val page = params.key ?: 1
        val news = getNewsUseCase(page).single()
        LoadResult.Page(
            news,
            if (page == 1) null else page - 1,
            if (news.isEmpty()) null else page + 1,
        )
    } catch (error: Exception) {
        LoadResult.Error(error)
    }

}

class NewsRemoteMediator @Inject constructor(
    private val database: AppDatabase,
    private val preference: Preference,
    private val getNewsUseCase: GetNewsUseCase,
) : RemoteMediator<Int, NewsLocal>() {

    override suspend fun initialize(): InitializeAction {
        return if (preference.isNeedToUpdateSettings()) InitializeAction.LAUNCH_INITIAL_REFRESH
        else InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsLocal>,
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(true)
                    lastItem.loadedFromPage
                }
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(true)
            }
            val response = getNewsUseCase(page).single()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) database.newsDao.clearAll()
                database.newsDao.insert(response)
                preference.saveUpdateNewsDate()
            }
            return MediatorResult.Success(response.isEmpty())
        } catch (error: Exception) {
            return MediatorResult.Error(error)
        }
    }

}

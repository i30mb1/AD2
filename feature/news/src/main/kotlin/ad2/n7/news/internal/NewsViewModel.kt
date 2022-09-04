package ad2.n7.news.internal

import ad2.n7.news.internal.domain.model.NewsVO
import ad2.n7.news.internal.domain.usecase.GetNewsUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class NewsViewModel @AssistedInject constructor(
    private val getNewsUseCase: GetNewsUseCase,
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

//    private lateinit var newsDao: NewsDao
//    var news: LiveData<PagedList<NewsModel>>? = null
//        private set
//    private var pageNews = 1

    init {
        setupLiveDataNews()
    }

    private fun setupLiveDataNews() {
        getNewsUseCase()
            .onEach { list -> state.emit(State.Data(list)) }
            .catch { error -> }
            .launchIn(viewModelScope)
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
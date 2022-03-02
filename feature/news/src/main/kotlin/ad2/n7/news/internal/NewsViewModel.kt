package ad2.n7.news.internal

import androidx.lifecycle.ViewModel
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class NewsViewModel @AssistedInject constructor(
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): NewsViewModel
    }

//    private lateinit var newsDao: NewsDao
//    var news: LiveData<PagedList<NewsModel>>? = null
//        private set
//    private var pageNews = 1

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
package n7.ad2.news

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedList.BoundaryCallback
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import n7.ad2.news.db.NewsDao
import n7.ad2.news.db.NewsModel
import n7.ad2.news.db.NewsRoomDatabase
import javax.inject.Inject

class NewsViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {

    var isLoading = ObservableBoolean(false)
    private lateinit var newsDao: NewsDao
    var news: LiveData<PagedList<NewsModel>>? = null
        private set
    private var pageNews = 1

    init {
        setupLiveDataNews()
    }

    private fun setupLiveDataNews() {
        newsDao = NewsRoomDatabase.getDatabase(application).steamNewsDao()
        val dataSource = newsDao.dataSourceNews
        val config = PagedList.Config.Builder()
            .setPageSize(12)
            .setInitialLoadSizeHint(12)
            .setPrefetchDistance(3)
            .setEnablePlaceholders(true)
            .build()
        news = LivePagedListBuilder(dataSource, config).setBoundaryCallback(object : BoundaryCallback<NewsModel>() {
            override fun onItemAtEndLoaded(itemAtEnd: NewsModel) {
                super.onItemAtEndLoaded(itemAtEnd)
                pageNews++
                val data = Data.Builder().putInt(NewsWorker.PAGE, pageNews).build()
                val worker = OneTimeWorkRequest.Builder(NewsWorker::class.java).setInputData(data).build()
                WorkManager.getInstance(application).enqueue(worker)
                WorkManager.getInstance(application).getWorkInfoByIdLiveData(worker.id).observeForever { workInfo ->
                    if (workInfo != null) {
                        if (workInfo.state.isFinished) {
                            isLoading.set(false)
                        } else {
                            isLoading.set(true)
                        }
                    }
                }
            }
        }).build()
    }
}
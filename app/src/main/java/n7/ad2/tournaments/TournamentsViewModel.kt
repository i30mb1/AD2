package n7.ad2.tournaments

import android.app.Application
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class TournamentsViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {

    private var page = 0
//    val tournamentsGames: LiveData<PagedList<TournamentGame>>
//        get() {
//            val dataSource: Factory<Int, TournamentGame> = gamesDao.dataSourceGames
//            val config: Config = Builder()
//                .setPageSize(30)
//                .setInitialLoadSizeHint(30)
//                .setPrefetchDistance(10)
//                .setEnablePlaceholders(true).build()
//            return LivePagedListBuilder<Int, TournamentGame>(dataSource, config).setBoundaryCallback(object : BoundaryCallback<TournamentGame?>() {
//                override fun onItemAtEndLoaded(itemAtEnd: TournamentGame) {
//                    super.onItemAtEndLoaded(itemAtEnd)
//                    page = page + 30
//                    val data = Data.Builder().putInt(TournamentsWorker.PAGE, page).build()
//                    val worker = OneTimeWorkRequest.Builder(TournamentsWorker::class.java).setInputData(data).build()
//                    WorkManager.getInstance().enqueue(worker)
//                    WorkManager.getInstance().getWorkInfoByIdLiveData(worker.id).observeForever { workInfo ->
//                        if (workInfo != null) {
//                            if (workInfo.state.isFinished) {
//                                isLoading.set(false)
//                            } else {
//                                isLoading.set(true)
//                            }
//                        }
//                    }
//                }
//            }).build()


}
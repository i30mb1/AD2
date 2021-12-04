package n7.ad2.ui.splash.domain.usecase

import android.app.Application
import javax.inject.Inject

class LoadNewsUseCase @Inject constructor(
    private val application: Application,
) {

    operator fun invoke() {
//        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//        val data = Data.Builder()
//            .putBoolean(NewsWorker.DELETE_TABLE, true)
//            .build()
//        val worker = OneTimeWorkRequest.Builder(NewsWorker::class.java)
//            .setInputData(data)
//            .setConstraints(constraints)
//            .build()
//        WorkManager.getInstance(application).beginUniqueWork(NewsWorker.TAG, ExistingWorkPolicy.APPEND, worker).enqueue()
    }

}
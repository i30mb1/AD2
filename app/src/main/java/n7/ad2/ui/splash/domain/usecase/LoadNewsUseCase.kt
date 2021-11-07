package n7.ad2.ui.splash.domain.usecase

import android.app.Application
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import n7.ad2.news.NewsWorker
import javax.inject.Inject

class LoadNewsUseCase @Inject constructor(
    private val application: Application,
) {

    operator fun invoke() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val data = Data.Builder()
            .putBoolean(NewsWorker.DELETE_TABLE, true)
            .build()
        val worker = OneTimeWorkRequest.Builder(NewsWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(application).beginUniqueWork(NewsWorker.TAG, ExistingWorkPolicy.APPEND, worker).enqueue()
    }

}
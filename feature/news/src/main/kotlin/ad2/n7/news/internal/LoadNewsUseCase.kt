package ad2.n7.news.internal

import javax.inject.Inject

class LoadNewsUseCase @Inject constructor(
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
package n7.ad2.database_guides.internal.worker

import android.app.Application
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope

class DatabaseWorker(
    val application: Application,
    workerParameters: WorkerParameters,
) : CoroutineWorker(application, workerParameters) {

//    @Inject
//    lateinit var populateHeroesDatabaseUseCase: PopulateHeroesDatabaseUseCase

//    @Inject
//    lateinit var populateItemsDatabaseUseCase: PopulateItemsDatabaseUseCase

    override suspend fun doWork(): Result = coroutineScope {
//        DaggerDatabaseComponent.factory()
//            .create((application as HasDependencies).dependenciesMap[DatabaseDependencies::class.java] as DatabaseDependencies)
//            .inject(this@DatabaseWorker)
        try {
//            populateHeroesDatabaseUseCase().single()
//            populateItemsDatabaseUseCase().single()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}
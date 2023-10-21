package n7.ad2.database_guides.internal.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.single
import n7.ad2.android.HasDependencies
import n7.ad2.database_guides.api.DatabaseDependencies
import n7.ad2.database_guides.internal.di.DaggerDatabaseComponent
import n7.ad2.database_guides.internal.domain.usecase.PopulateItemsDatabaseUseCase

class DatabaseWorker(
    val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    @Inject lateinit var populateItemsDatabaseUseCase: PopulateItemsDatabaseUseCase

    override suspend fun doWork(): Result = coroutineScope {
        DaggerDatabaseComponent.factory()
            .create((context as HasDependencies).dependenciesMap[DatabaseDependencies::class.java] as DatabaseDependencies)
            .inject(this@DatabaseWorker)
        try {
            populateItemsDatabaseUseCase().single()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}

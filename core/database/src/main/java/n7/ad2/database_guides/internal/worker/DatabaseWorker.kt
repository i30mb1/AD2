package n7.ad2.database_guides.internal.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.single
import n7.ad2.android.HasDependencies
import n7.ad2.database_guides.api.DatabaseDependencies
import n7.ad2.database_guides.internal.di.DaggerDatabaseComponent
import n7.ad2.database_guides.internal.domain.usecase.PopulateItemsDatabaseUseCase
import n7.ad2.logger.AD2Logger
import javax.inject.Inject

class DatabaseWorker(
    val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    //    @Inject
//    lateinit var populateHeroesDatabaseUseCase: PopulateHeroesDatabaseUseCase
//
    @Inject
    lateinit var populateItemsDatabaseUseCase: PopulateItemsDatabaseUseCase

    @Inject
    lateinit var logger: AD2Logger

    override suspend fun doWork(): Result = coroutineScope {
        context as HasDependencies
        context.applicationContext as HasDependencies
        DaggerDatabaseComponent.factory()
            .create((context as HasDependencies).dependenciesMap[DatabaseDependencies::class.java] as DatabaseDependencies)
            .inject(this@DatabaseWorker)
        logger.log("do work")
        try {
//            populateHeroesDatabaseUseCase().single()
            populateItemsDatabaseUseCase().single()
            Result.success(
                Data.Builder()
                    .putBoolean("is context hasDependencies", context is HasDependencies)
                    .putBoolean("is context.applicationContext hasDependencies", context.applicationContext is HasDependencies)
                    .build()
            )
        } catch (e: Exception) {
            Result.failure()
        }
    }

}
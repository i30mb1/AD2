package n7.ad2.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import n7.ad2.ui.MyApplication
import n7.ad2.ui.splash.domain.interactor.PopulateHeroesDatabaseInteractor
import n7.ad2.ui.splash.domain.interactor.PopulateItemsDatabaseInteractor
import javax.inject.Inject

class DatabaseWorker(
        val context: Context,
        workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    @Inject
    lateinit var populateHeroesDatabaseInteractor: PopulateHeroesDatabaseInteractor

    @Inject
    lateinit var populateItemsDatabaseInteractor: PopulateItemsDatabaseInteractor

    override suspend fun doWork(): Result = coroutineScope {
        (context as MyApplication).component.inject(this@DatabaseWorker)

        populateHeroesDatabaseInteractor.invoke()
        populateItemsDatabaseInteractor.invoke()

        Result.success()
    }

}
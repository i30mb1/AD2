package ad2.n7.news.internal

import ad2.n7.news.api.NewsDependencies
import ad2.n7.news.internal.di.DaggerNewsComponent
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import n7.ad2.android.HasDependencies

class NewsWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = coroutineScope {
        DaggerNewsComponent.factory()
            .create((context as HasDependencies).dependenciesMap[NewsDependencies::class.java] as NewsDependencies)
            .inject(this@NewsWorker)

        Result.failure()
    }

}
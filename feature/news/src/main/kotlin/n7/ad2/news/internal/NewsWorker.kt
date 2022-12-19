package n7.ad2.news.internal

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import n7.ad2.android.HasDependencies
import n7.ad2.news.api.NewsDependencies
import n7.ad2.news.internal.di.DaggerNewsComponent
import n7.ad2.news.internal.domain.usecase.GetNewsUseCase
import javax.inject.Inject

class NewsWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    companion object {
        val request: OneTimeWorkRequest = OneTimeWorkRequestBuilder<NewsWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
    }

    @Inject lateinit var getNewsUseCase: GetNewsUseCase

    override suspend fun doWork(): Result = coroutineScope {
        DaggerNewsComponent.factory()
            .create((context as HasDependencies).dependenciesMap[NewsDependencies::class.java] as NewsDependencies)
            .inject(this@NewsWorker)
        setForeground(createForegroundInfo())
        Result.failure()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo()
    }

    private fun notification(): Notification {
        val intent = WorkManager.getInstance(context)
            .createCancelPendingIntent(id)
        return NotificationCompat.Builder(applicationContext, "7")
            .setContentTitle("Loading News")
//            .setContentText(progress)
            .addAction(android.R.drawable.ic_delete, "cancel", intent)
            .build()

    }

    private fun createForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(7, notification())
    }

}
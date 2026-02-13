package n7.ad2.news.ui.internal

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
import n7.ad2.items.domain.usecase.GetNewsUseCase
import n7.ad2.news.ui.api.NewsDependencies
import n7.ad2.news.ui.internal.di.DaggerNewsComponent
import javax.inject.Inject

internal class NewsWorker(private val context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

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

    override suspend fun getForegroundInfo(): ForegroundInfo = createForegroundInfo()

    private fun notification(): Notification {
        val intent = WorkManager.getInstance(context)
            .createCancelPendingIntent(id)
        return NotificationCompat.Builder(applicationContext, "7")
            .setContentTitle("Loading News")
//            .setContentText(progress)
            .addAction(android.R.drawable.ic_delete, "cancel", intent)
            .build()
    }

    private fun createForegroundInfo(): ForegroundInfo = ForegroundInfo(7, notification())
}

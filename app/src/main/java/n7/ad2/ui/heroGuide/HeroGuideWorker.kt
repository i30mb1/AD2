@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.ui.heroGuide

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.coroutineScope
import n7.ad2.AD2Logger
import n7.ad2.R
import n7.ad2.createNotificationChannel
import n7.ad2.isChannelNotCreated
import n7.ad2.ui.MyApplication
import n7.ad2.ui.heroGuide.domain.usecase.ConvertLocalGuideJsonToLocalGuide
import n7.ad2.ui.heroGuide.domain.usecase.GetLocalGuideJsonUseCase
import n7.ad2.ui.heroGuide.domain.usecase.SaveLocalGuideUseCase
import java.util.*
import javax.inject.Inject


class HeroGuideWorker(
    val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        const val RESULT = "RESULT"

        @SuppressLint("UnsafeOptInUsageError")
        fun getRequest(heroName: String): OneTimeWorkRequest {
            val data = workDataOf(HERO_NAME to heroName)
            return OneTimeWorkRequestBuilder<HeroGuideWorker>()
//                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(data)
                .build()
        }
    }

    private val notificationId = 1
    private val channelId = "guide_worker"
    private val channelName = "Guide"
    private val notificationTitle = applicationContext.getString(R.string.notification_title_guide)

    @Inject
    lateinit var saveLocalGuideUseCase: SaveLocalGuideUseCase

    @Inject
    lateinit var convertLocalGuideJsonToLocalGuide: ConvertLocalGuideJsonToLocalGuide

    @Inject
    lateinit var getLocalGuideJson: GetLocalGuideJsonUseCase

    @Inject
    lateinit var aD2Logger: AD2Logger

    override suspend fun doWork(): Result = coroutineScope {
        (context as MyApplication).component.inject(this@HeroGuideWorker)

//        startForegroundIfNotificationEnabled()

        val heroName = inputData.getString(HERO_NAME)!!

        try {
            val localGuideJson = getLocalGuideJson(heroName)
            val localGuide = convertLocalGuideJsonToLocalGuide(localGuideJson, heroName)
            saveLocalGuideUseCase(localGuide)

            aD2Logger.log("guide loaded for $heroName")
            Result.success()
        } catch (e: Exception) {
            val data = workDataOf(RESULT to e.toString())
            aD2Logger.log("guide load fails for $heroName")
            Result.failure(data)
        }
    }

    private suspend fun startForegroundIfNotificationEnabled() {
        //  https://proandroiddev.com/android-foreground-service-restrictions-d3baa93b2f70 not all foreground works
        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            if (applicationContext.isChannelNotCreated(channelId)) applicationContext.createNotificationChannel(channelId, channelName)

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_red)
                .setContentTitle(notificationTitle)
                .build()
            setForeground(ForegroundInfo(notificationId, notification))
        }
    }

}
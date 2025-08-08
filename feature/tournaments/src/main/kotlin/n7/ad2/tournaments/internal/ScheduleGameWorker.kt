package n7.ad2.tournaments.internal

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ScheduleGameWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val message = inputData.getString("message")

        // TODO: Replace with proper MainActivity reference
        val notificationIntent = Intent(applicationContext, Class.forName("n7.ad2.ui.MainActivity"))
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val options = BitmapFactory.Options()
        val largeIcon = BitmapFactory.decodeResource(
            applicationContext.resources,
            applicationContext.resources.getIdentifier("ic_launcher_red", "mipmap", applicationContext.packageName),
            options
        )

        val builder = NotificationCompat.Builder(applicationContext, "AD2")
            .setAutoCancel(true)
            .setContentTitle(
                applicationContext.getString(
                    applicationContext.resources.getIdentifier("notification_game_title", "string", applicationContext.packageName)
                )
            )
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setContentInfo(
                applicationContext.getString(
                    applicationContext.resources.getIdentifier("notification_game_info", "string", applicationContext.packageName)
                )
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(
                Uri.parse(
                    "android.resource://${applicationContext.packageName}/${
                        applicationContext.resources.getIdentifier("does_this_unit_have_a_soul", "raw", applicationContext.packageName)
                    }"
                )
            )
            .setSmallIcon(applicationContext.resources.getIdentifier("ic_launcher_red", "mipmap", applicationContext.packageName))
            .setLargeIcon(largeIcon)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        return if (notificationManager != null) {
            notificationManager.notify(7, builder.build())
            Result.success()
        } else {
            Result.failure()
        }
    }
}

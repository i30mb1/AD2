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
        val message = inputData.getString("message") ?: return Result.failure()

        return try {
            // Get resource IDs once to avoid multiple lookups
            val resources = applicationContext.resources
            val packageName = applicationContext.packageName
            val iconResourceId = resources.getIdentifier("ic_launcher_red", "mipmap", packageName)
            val titleResourceId = resources.getIdentifier("notification_game_title", "string", packageName)
            val infoResourceId = resources.getIdentifier("notification_game_info", "string", packageName)
            val soundResourceId = resources.getIdentifier("does_this_unit_have_a_soul", "raw", packageName)

            val notificationIntent = Intent(applicationContext, Class.forName("n7.ad2.ui.MainActivity"))
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )

            val largeIcon = BitmapFactory.decodeResource(resources, iconResourceId, BitmapFactory.Options())

            val builder = NotificationCompat.Builder(applicationContext, "AD2")
                .setAutoCancel(true)
                .setContentTitle(applicationContext.getString(titleResourceId))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setContentInfo(applicationContext.getString(infoResourceId))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Uri.parse("android.resource://$packageName/$soundResourceId"))
                .setSmallIcon(iconResourceId)
                .setLargeIcon(largeIcon)

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.notify(7, builder.build())
            Result.success()
        } catch (e: ClassNotFoundException) {
            Result.failure()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

package n7.ad2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun Context.isChannelEnabled(channelId: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel: NotificationChannel? = notificationManager.getNotificationChannel(channelId)
        return notificationChannel?.importance != NotificationManager.IMPORTANCE_NONE
    }
    return true
}
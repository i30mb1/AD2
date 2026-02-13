package n7.ad2.android

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import java.util.Calendar

class RemindersManager(private val context: Application) {

    companion object {
        const val REQUEST_CODE = 111
    }

    private val alarmManager = context.getSystemService<AlarmManager>()!!

    @RequiresPermission(value = "android.permission.SCHEDULE_EXACT_ALARM")
    fun startReminder(reminderTime: String = "08:00", reminderId: Int = REQUEST_CODE) {
        val (hours, min) = reminderTime.split(":").map { it.toInt() }
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, min)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            return
        }

        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent), pendingIntent)
    }

    fun stopReminder(_reminderId: Int = REQUEST_CODE) {
        val intent = Intent(context, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(context, reminderId, intent, 0)
//        alarmManager.cancel(pendingIntent)
    }
}

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // do something
    }
}

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // When a user shuts down his phone all the alarms that you have scheduled before will disappear
        if (intent.action == "android.intent.action.BOOT_COMPLETED") println("")
    }
}

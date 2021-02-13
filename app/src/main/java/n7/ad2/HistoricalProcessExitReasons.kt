package n7.ad2

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

class HistoricalProcessExitReasons {

    @RequiresApi(Build.VERSION_CODES.R)
    fun get(application: Application) {
        val activityManager = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val reasonsList = activityManager.getHistoricalProcessExitReasons(null, 0, 10)
        for (reason in reasonsList) {

        }
    }

}
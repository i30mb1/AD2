package n7.ad2.init

import android.app.ActivityManager
import android.app.Application
import android.app.ApplicationExitInfo
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import n7.ad2.AppInformation
import n7.ad2.app.logger.Logger

class HistoricalProcessExitReasonsInitializer : Initializer {

    override fun init(app: Application, logger: Logger, appInformation: AppInformation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HistoricalProcessExitReasons().init(app, logger)
        }
    }

    private class HistoricalProcessExitReasons {

        @RequiresApi(Build.VERSION_CODES.R)
        fun init(application: Application, logger: Logger) {
            val activityManager = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val exitInfoList = activityManager.getHistoricalProcessExitReasons(null, 0, 1)
            for (exitInfo in exitInfoList) {
                val description = exitInfo.description
                val reason = exitInfo.reason
                val stackTrace: String? = exitInfo.traceInputStream?.bufferedReader().use { bufferedReader -> bufferedReader?.readText() }
                if (reason == ApplicationExitInfo.REASON_ANR) stackTrace?.length
                if (description != null) logger.log("$reason: $description")
            }
        }
    }
}

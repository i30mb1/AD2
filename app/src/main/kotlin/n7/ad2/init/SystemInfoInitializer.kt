package n7.ad2.init

import android.app.ActivityManager
import android.app.Application
import android.os.Build
import android.provider.Settings
import androidx.core.content.getSystemService
import n7.ad2.AppInformation
import n7.ad2.logger.AD2Logger

class SystemInfoInitializer : Initializer {

    override fun init(app: Application, logger: AD2Logger, appInformation: AppInformation) {
        val androidID = Settings.Secure.getString(app.contentResolver, Settings.Secure.ANDROID_ID)
        val androidVersion = Build.VERSION.SDK_INT
        val cpuCount = Runtime.getRuntime().availableProcessors()
        logger.log("SYSTEM INFO:")
        logger.log("------")
        logger.log("IS_DEBUG = ${appInformation.isDebug}")
        logger.log("ANDROID_ID = $androidID")
        logger.log("ANDROID_VERSION = $androidVersion")
        logger.log("CPU_COUNT = $cpuCount")
        logger.log("memory = ${getMemoryInfo(app)}")
        logger.log("------")
    }

    private fun getMemoryInfo(app: Application): String {
        val activityManager = app.getSystemService<ActivityManager>()
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        return "${memoryInfo.availMem / 1_048_576}/${memoryInfo.totalMem / 1_048_576} mb"
    }

}
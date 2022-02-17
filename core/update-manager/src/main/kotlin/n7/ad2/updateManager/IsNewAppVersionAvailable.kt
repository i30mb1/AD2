package n7.ad2.updateManager

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestAppUpdateInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import n7.ad2.logger.AD2Logger
import javax.inject.Inject

class IsNewAppVersionAvailable @Inject constructor(
    private val appUpdateManager: AppUpdateManager,
    private val logger: AD2Logger,
) {

    suspend operator fun invoke(): Flow<Boolean> = flow {
        val appUpdateInfo = appUpdateManager.requestAppUpdateInfo()
        val lastUpdateDay = appUpdateInfo.clientVersionStalenessDays()
        val updatePriority = appUpdateInfo.updatePriority()
        logger.log("last update was $lastUpdateDay days")
        logger.log("update priority $updatePriority")
        val isUpdateAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
        val updateTypeAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
        when {
            isUpdateAvailable && updateTypeAllowed -> emit(true)
            else -> emit(false)
        }
    }

}

//private fun loadNewVersionFromMarket() {
//    try {
//        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
//    } catch (a: ActivityNotFoundException) {
//        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
//    }
//}

//private fun loadNewVersionFromGitHub() {
//    val GITHUB_LAST_APK_URL = "https://github.com/i30mb1/AD2/blob/master/app-release.apk?raw=true"
//    try {
//        val request = DownloadManager.Request(Uri.parse(GITHUB_LAST_APK_URL))
//        request.setDescription("new app version")
//        request.setTitle("AD2")
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setMimeType("application/vnd.android.package-archive")
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "AD2")
//        val manager = getSystemService(FragmentActivity.DOWNLOAD_SERVICE) as DownloadManager
//        manager.enqueue(request)
//    } catch (e: SecurityException) {
//        e.printStackTrace()
//    }
//}
package n7.ad2.updatemanager

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestAppUpdateInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import n7.ad2.app.logger.Logger

class IsNewAppVersionAvailable @Inject constructor(
    private val appUpdateManager: AppUpdateManager,
    private val logger: Logger,
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

package n7.ad2

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.Lazy
import n7.ad2.logger.AD2Logger
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CheckAppUpdateUseCase @Inject constructor(
    private val appUpdateManager: Lazy<AppUpdateManager>,
    private val logger: AD2Logger,
) {

    suspend operator fun invoke(): Boolean = suspendCoroutine { continuation ->
        appUpdateManager.get().appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val lastUpdateDay = appUpdateInfo.clientVersionStalenessDays()
            val updatePriority = appUpdateInfo.updatePriority()
            logger.log("last update was $lastUpdateDay days")
            logger.log("update priority $updatePriority")
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                continuation.resume(true)
            }
            continuation.resume(false)
        }
    }

}
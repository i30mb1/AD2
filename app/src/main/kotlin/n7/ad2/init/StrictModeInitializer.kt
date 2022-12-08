package n7.ad2.init

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import n7.ad2.AppInformation
import n7.ad2.logger.Logger

class StrictModeInitializer : Initializer {

    override fun init(app: Application, logger: Logger, appInformation: AppInformation) {
        if (false) {
            Handler(Looper.getMainLooper()).postAtFrontOfQueue {
                StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder()
                        .detectDiskWrites()
                        .detectNetwork()
                        .detectCustomSlowCalls()
                        .detectResourceMismatches()
                        .penaltyLog()
                        .build()
                )
                StrictMode.setVmPolicy(
                    StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build()
                )
            }
        }
    }

}
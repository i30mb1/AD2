package n7.ad2.init

import android.app.Application
import androidx.core.performance.DevicePerformance
import n7.ad2.AppInformation
import n7.ad2.app.logger.Logger

class DevicePerformanceInitializer : Initializer {

    override fun init(app: Application, logger: Logger, appInformation: AppInformation) {
        val devicePerf: DevicePerformance = DevicePerformance.create(app)
        logger.log("Device Performance Class = ${devicePerf.mediaPerformanceClass}")
    }
}

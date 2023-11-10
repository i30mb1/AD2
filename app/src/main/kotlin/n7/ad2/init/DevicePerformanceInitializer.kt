package n7.ad2.init

import android.app.Application
import androidx.core.performance.DevicePerformance
import androidx.core.performance.play.services.PlayServicesDevicePerformance
import n7.ad2.AppInformation
import n7.ad2.app.logger.Logger

class DevicePerformanceInitializer : Initializer {

    override fun init(app: Application, logger: Logger, appInformation: AppInformation) {
        val devicePerf: DevicePerformance = PlayServicesDevicePerformance(app)
        logger.log("Device Performance Class = ${devicePerf.mediaPerformanceClass}")
    }
}

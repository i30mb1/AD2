package n7.ad2.init

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.logger.Logger

interface Initializer {

    fun init(app: Application, logger: Logger, appInformation: AppInformation)

}
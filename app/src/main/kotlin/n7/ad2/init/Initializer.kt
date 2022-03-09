package n7.ad2.init

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.logger.AD2Logger

interface Initializer {

    fun init(app: Application, logger: AD2Logger, appInformation: AppInformation)

}
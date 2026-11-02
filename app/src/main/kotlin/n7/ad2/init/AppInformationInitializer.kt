package n7.ad2.init

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.AppInformationHolder
import n7.ad2.app.logger.Logger
import n7.ad2.common.application.Resources
import n7.ad2.common.application.ResourcesHolder

/** The resources and the information about the app for the code without injection; runs before the other initializers */
class AppInformationInitializer : Initializer {

    override fun init(app: Application, logger: Logger, appInformation: AppInformation) {
        val resources = Resources(app)
        ResourcesHolder.set(resources)
        AppInformationHolder.set(AppInformation(resources))
    }
}

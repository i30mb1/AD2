package n7.ad2.init

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.AppInformationHolder
import n7.ad2.app.logger.Logger
import n7.ad2.common.application.Resources
import n7.ad2.common.application.ResourcesHolder
import yandex.metrics.YandexMetricsInit

class YandexMetricsInitializer : Initializer {

    override fun init(app: Application, logger: Logger, appInformation: AppInformation) {
        val resources = Resources(app)
        ResourcesHolder.set(resources)
        val aD2AppInformation = AppInformation(resources)
        AppInformationHolder.set(aD2AppInformation)
        YandexMetricsInit(aD2AppInformation)(app)
    }
}

package n7.ad2.init

import android.app.Application
import javax.inject.Inject
import n7.ad2.AppInformation
import n7.ad2.app.logger.Logger
import yandex.metrics.YandexMetricsInit

class YandexMetricsInitializer @Inject constructor(
    private val yandexMetricsInit: YandexMetricsInit,
) : Initializer {

    override fun init(app: Application, logger: Logger, appInformation: AppInformation) {
        yandexMetricsInit(app)
    }
}

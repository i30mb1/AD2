package n7.ad2.init

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.app.logger.Logger
import yandex.metrics.YandexMetricsInit
import javax.inject.Inject

class YandexMetricsInitializer @Inject constructor(
    private val yandexMetricsInit: YandexMetricsInit,
) : Initializer {

    override fun init(app: Application, logger: Logger, appInformation: AppInformation) {
        yandexMetricsInit(app)
    }

}
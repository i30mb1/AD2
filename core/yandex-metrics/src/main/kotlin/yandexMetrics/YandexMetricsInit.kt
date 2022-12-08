package yandexMetrics

import android.app.Application
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import n7.ad2.AppInformation
import javax.inject.Inject


class YandexMetricsInit @Inject constructor(
    private val appInformation: AppInformation,
) {

    operator fun invoke(application: Application) {
        val config = YandexMetricaConfig
            .newConfigBuilder("cbceb5c6-16ce-4905-bc4f-f99fd8b82652")
            .withAppVersion(appInformation.appVersion)
            .withLocationTracking(false)
            .withLogs()
            .build()
        YandexMetrica.activate(application, config)
        YandexMetrica.enableActivityAutoTracking(application)
    }

}
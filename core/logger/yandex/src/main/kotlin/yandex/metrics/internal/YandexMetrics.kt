package yandex.metrics.internal

import com.yandex.metrica.YandexMetrica
import n7.ad2.app.logger.AdditionalLogger

internal class YandexMetrics : AdditionalLogger {

    override fun logEvent(event: String, params: Map<String, Any>) {
        YandexMetrica.reportEvent(event, params)
    }
}

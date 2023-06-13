package yandex.metrics.internal

import com.yandex.metrica.YandexMetrica
import yandex.metrics.AppMetrics

internal class YandexMetrics : AppMetrics {

    override fun logEvent(event: String, params: Map<String, Any>) {
        YandexMetrica.reportEvent(event, params)
    }
}

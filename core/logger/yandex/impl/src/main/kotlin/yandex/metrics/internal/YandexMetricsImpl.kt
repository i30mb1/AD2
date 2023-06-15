package yandex.metrics.internal

import com.yandex.metrica.YandexMetrica
import yandex.metrics.YandexMetrics

internal class YandexMetricsImpl : YandexMetrics {

    override fun logEvent(event: String, params: Map<String, Any>) {
        YandexMetrica.reportEvent(event, params)
    }
}

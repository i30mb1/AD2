package yandex.metrics

import com.yandex.metrica.YandexMetrica

class YandexMetrics : AppMetrics {

    override fun logEvent(event: String, params: Map<String, Any>) {
        YandexMetrica.reportEvent(event, params)
    }

}

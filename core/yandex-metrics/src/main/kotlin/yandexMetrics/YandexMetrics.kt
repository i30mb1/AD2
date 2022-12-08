package yandexMetrics

import com.yandex.metrica.YandexMetrica
import n7.ad2.AppMetrics
import javax.inject.Inject

class YandexMetrics @Inject constructor() : AppMetrics {

    override fun logEvent(event: String, params: Map<String, Any>) {
        YandexMetrica.reportEvent(event, params)
    }

}
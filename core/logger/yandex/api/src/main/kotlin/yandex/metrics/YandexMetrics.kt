package yandex.metrics

interface YandexMetrics {
    fun logEvent(event: String, params: Map<String, Any>)
}

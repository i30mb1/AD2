package yandex.metrics

interface AppMetrics {
    fun logEvent(event: String, params: Map<String, Any>)
}

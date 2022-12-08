package n7.ad2

interface AppMetrics {
    fun logEvent(event: String, params: Map<String, Any>)
}
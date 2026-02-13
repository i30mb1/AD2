package n7.ad2.app.logger

interface AdditionalLogger {
    fun logEvent(event: String, params: Map<String, Any>)
}

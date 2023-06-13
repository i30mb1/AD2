package n7.ad2.logger

import kotlinx.coroutines.flow.Flow
import n7.ad2.logger.internal.AppLogger
import yandex.metrics.AppMetrics

interface Logger {
    fun log(text: String)
    fun logEvent(event: String, params: Map<String, Any> = emptyMap())
    fun getSubscriptionCount(): Int
    fun getLogFlow(): Flow<AppLog>
}

object LoggerFactory {
    private val logger: Logger? = null

    

}

fun Logger(appMetrics: AppMetrics): Logger {
    return AppLogger(appMetrics)
}

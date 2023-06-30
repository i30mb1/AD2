package n7.ad2.app.logger

import kotlinx.coroutines.flow.Flow
import n7.ad2.app.logger.internal.LoggerImpl

interface Logger {
    fun log(text: String, params: Map<String, Any> = emptyMap())
    fun getSubscriptionCount(): Int
    fun getLogFlow(): Flow<AppLog>
}

fun Logger(aditionalLoggers: List<AdditionalLogger> = emptyList()): Logger {
    return LoggerImpl(aditionalLoggers)
}

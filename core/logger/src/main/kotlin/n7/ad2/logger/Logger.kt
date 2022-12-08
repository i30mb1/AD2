package n7.ad2.logger

import kotlinx.coroutines.flow.Flow

interface Logger {
    fun log(text: String)
    fun logEvent(event: String, params: Map<String, Any> = emptyMap())
    fun getSubscriptionCount(): Int
    fun getLogFlow(): Flow<AppLog>
}
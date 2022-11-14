package n7.ad2.logger

import kotlinx.coroutines.flow.Flow

interface Logger {
    fun log(text: String)
    fun getSubscriptionCount(): Int
    fun getLogFlow(): Flow<AppLog>
}
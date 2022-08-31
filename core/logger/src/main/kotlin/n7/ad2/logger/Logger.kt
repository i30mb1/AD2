package n7.ad2.logger

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface Logger {
    fun log(text: String)
    fun getSubscriptionCount(): Int
    fun getLogFlow(): SharedFlow<AD2Log>
}

class LoggerFake : Logger {
    override fun log(text: String) = Unit
    override fun getSubscriptionCount(): Int = 0
    override fun getLogFlow(): SharedFlow<AD2Log> = MutableSharedFlow()
}
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import n7.ad2.logger.AD2Log
import n7.ad2.logger.Logger

class LoggerFake : Logger {
    override fun log(text: String) = Unit
    override fun getSubscriptionCount(): Int = 0
    override fun getLogFlow(): Flow<AD2Log> = emptyFlow()
}
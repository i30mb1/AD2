import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import n7.ad2.app.logger.AppLog
import n7.ad2.app.logger.Logger

class LoggerFake : Logger {
    override fun log(text: String, params: Map<String, Any>) = Unit
    override fun getSubscriptionCount(): Int = 0
    override fun getLogFlow(): Flow<AppLog> = emptyFlow()
}

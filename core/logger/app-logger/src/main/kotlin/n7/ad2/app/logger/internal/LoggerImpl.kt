package n7.ad2.app.logger.internal

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.runningFold
import n7.ad2.app.logger.AdditionalLogger
import n7.ad2.app.logger.Logger
import n7.ad2.app.logger.model.AppLog

internal class LoggerImpl(private val aditionalLoggers: List<AdditionalLogger>) : Logger {

    private val _dataFlow = MutableSharedFlow<AppLog>(
        replay = 100,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val dataFlow: SharedFlow<AppLog> = _dataFlow.asSharedFlow()

    override fun log(text: String, params: Map<String, Any>) {
        _dataFlow.tryEmit(AppLog(text))
//        aditionalLoggers.forEach { logger -> logger.logEvent(text, params) }
    }

    override fun getSubscriptionCount(): Int = _dataFlow.subscriptionCount.value

    override fun getLogFlow(): SharedFlow<AppLog> = dataFlow

    override fun getLogsFlow(): Flow<List<AppLog>> = dataFlow.runningFold(emptyList()) { list: List<AppLog>, value: AppLog ->
        list + value
    }
}

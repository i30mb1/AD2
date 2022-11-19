package n7.ad2.logger

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject


class AppLogger @Inject constructor() : Logger {

    private val _dataFlow = MutableSharedFlow<AppLog>(
        replay = 100,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val dataFlow = _dataFlow.asSharedFlow()

    override fun log(text: String) {
        _dataFlow.tryEmit(AppLog(text))
    }

    override fun getSubscriptionCount(): Int = _dataFlow.subscriptionCount.value

    override fun getLogFlow(): SharedFlow<AppLog> = dataFlow

}
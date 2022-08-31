package n7.ad2.logger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import n7.ad2.coroutines.DispatchersProvider
import javax.inject.Inject


class AD2Logger @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: DispatchersProvider,
) : Logger {

    private val _dataFlow = MutableSharedFlow<AD2Log>(
        replay = 100,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val dataFlow = _dataFlow.asSharedFlow()

    override fun log(text: String) {
        coroutineScope.launch(dispatchers.IO) {
            _dataFlow.emit(AD2Log(text))
        }
    }

    override fun getSubscriptionCount(): Int = _dataFlow.subscriptionCount.value

    override fun getLogFlow(): SharedFlow<AD2Log> = dataFlow

}
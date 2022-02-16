package n7.ad2.logger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.ApplicationScope
import javax.inject.Inject

class AD2Log(val message: String)

@ApplicationScope
class AD2Logger @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: DispatchersProvider,
) {

    private val _dataFlow = MutableSharedFlow<AD2Log>(
        replay = 100,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val dataFlow = _dataFlow.asSharedFlow()

    fun log(text: String) {
        coroutineScope.launch(dispatchers.IO) {
            _dataFlow.emit(AD2Log(text))
        }
    }

    fun getLogFlow(): SharedFlow<AD2Log> = dataFlow

}
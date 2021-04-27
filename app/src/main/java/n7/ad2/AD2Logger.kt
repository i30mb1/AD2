package n7.ad2

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class AD2Log(val message: String)

@Singleton
class AD2Logger @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
) {

    private val dataFlow = MutableSharedFlow<AD2Log>(
        replay = 100,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun log(text: String) = coroutineScope.launch(ioDispatcher) {
        dataFlow.emit(AD2Log(text))
    }

    fun getLogFlow(): SharedFlow<AD2Log> = dataFlow.asSharedFlow()

}
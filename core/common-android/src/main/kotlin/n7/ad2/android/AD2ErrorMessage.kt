package n7.ad2.android

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

interface ErrorMessage {
    val error: Flow<String>
    suspend fun showError(throwable: Throwable)
}

class AD2ErrorMessage : ErrorMessage {
    private val _error = Channel<String>(Channel.BUFFERED)
    override val error = _error.receiveAsFlow()
    override suspend fun showError(throwable: Throwable) = _error.send(throwable.message ?: throwable.toString())
}
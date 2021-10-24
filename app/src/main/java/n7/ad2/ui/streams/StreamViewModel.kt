package n7.ad2.ui.streams

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import n7.ad2.ui.streams.usecase.GetStreamUrlsUseCase
import javax.inject.Inject

class StreamViewModel @Inject constructor(
    private val getStreamUrlsUseCase: GetStreamUrlsUseCase,
) : ViewModel() {

    private val _error = Channel<Throwable>(Channel.BUFFERED)
    val error = _error.receiveAsFlow()

    val url: MutableLiveData<String?> = MutableLiveData(null)

    fun load(streamerName: String) {
        getStreamUrlsUseCase(streamerName)
            .catch { _error.send(it) }
            .onEach { url.value = it }
            .launchIn(viewModelScope)
    }

}
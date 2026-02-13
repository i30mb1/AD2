package n7.ad2.streams.internal.stream

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.android.AD2ErrorMessage
import n7.ad2.android.ErrorMessage
import n7.ad2.streams.internal.stream.usecase.GetStreamUrlsUseCase

class StreamViewModel @AssistedInject constructor(private val getStreamUrlsUseCase: GetStreamUrlsUseCase) :
    ViewModel(),
    ErrorMessage by AD2ErrorMessage() {

    @AssistedFactory
    interface Factory {
        fun create(): StreamViewModel
    }

    val url: MutableStateFlow<String> = MutableStateFlow("")

    fun load(streamerName: String) {
        getStreamUrlsUseCase(streamerName)
            .catch { throwable -> showError(throwable) }
            .onEach { url.value = it }
            .launchIn(viewModelScope)
    }
}

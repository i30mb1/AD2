package n7.ad2.ui.streams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import n7.ad2.ui.streams.usecase.GetStreamUrlsUseCase
import javax.inject.Inject

class StreamViewModel @Inject constructor(
    private val getStreamUrlsUseCase: GetStreamUrlsUseCase,
) : ViewModel() {

    fun load(streamerName: String) {
        getStreamUrlsUseCase(streamerName)
            .launchIn(viewModelScope)
    }

}
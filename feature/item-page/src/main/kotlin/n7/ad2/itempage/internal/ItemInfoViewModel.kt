package n7.ad2.itempage.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import n7.ad2.AppInformation
import n7.ad2.itempage.internal.domain.usecase.GetItemInfoUseCase
import n7.ad2.itempage.internal.domain.vo.VOItemPage

internal class ItemInfoViewModel @AssistedInject constructor(
    private val appInfo: AppInformation,
    private val getItemInfoUseCase: GetItemInfoUseCase,
    @Assisted private val itemName: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(itemName: String): ItemInfoViewModel
    }

    sealed interface State {
        data object Loading : State
        data class Content(val page: VOItemPage) : State
        data class Error(val error: Throwable) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    /** Sound of the ability that is playing now, null when nothing plays. */
    private val _playingUrl = MutableStateFlow<String?>(null)
    val playingUrl: StateFlow<String?> = _playingUrl.asStateFlow()

    init {
        load()
    }

    fun load() {
        getItemInfoUseCase(itemName, appInfo.appLocale)
            .onStart { _state.value = State.Loading }
            .onEach { page -> _state.value = State.Content(page) }
            .catch { error -> _state.value = State.Error(error) }
            .launchIn(viewModelScope)
    }

    fun onStartPlaying(url: String) {
        _playingUrl.value = url
    }

    fun onEndPlaying() {
        _playingUrl.value = null
    }
}

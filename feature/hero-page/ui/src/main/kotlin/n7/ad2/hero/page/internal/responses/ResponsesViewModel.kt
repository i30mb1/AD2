package n7.ad2.hero.page.internal.responses

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
import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.android.AD2ErrorMessage
import n7.ad2.android.ErrorMessage
import n7.ad2.hero.page.internal.responses.domain.interactor.GetHeroResponsesInteractor
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse

class ResponsesViewModel @AssistedInject constructor(
    @Assisted private val heroName: String,
    private val appInformation: AppInformation,
    private val getHeroResponsesInteractor: GetHeroResponsesInteractor,
) : ViewModel(), ErrorMessage by AD2ErrorMessage() {

    @AssistedFactory
    interface Factory {
        fun create(heroName: String): ResponsesViewModel
    }

    sealed class State {
        data class Data(val list: List<VOResponse>) : State()
        data class Error(val error: Throwable) : State()
        object Loading : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        loadResponses(appInformation.appLocale)
    }

    fun loadResponses(appLocale: AppLocale) {
        getHeroResponsesInteractor(heroName, appLocale)
            .onEach { list -> _state.emit(State.Data(list)) }
            .catch { error -> _state.emit(State.Error(error)) }
            .launchIn(viewModelScope)
    }

    fun refreshResponses() {
        loadResponses(appInformation.appLocale)
    }

}
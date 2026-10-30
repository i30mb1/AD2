package n7.ad2.hero.page.internal.guides

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
import n7.ad2.hero.page.internal.guides.domain.interactor.GetVOHeroGuideUseCase
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide

class HeroGuideViewModel @AssistedInject constructor(
    @Assisted private val heroName: String,
    getVOHeroGuideUseCase: GetVOHeroGuideUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(heroName: String): HeroGuideViewModel
    }

    sealed class State {
        data class Data(val guide: VOGuide) : State()
        data class Error(val error: Throwable) : State()
        object Empty : State()
        object Loading : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        getVOHeroGuideUseCase(heroName)
            .onEach { guide -> _state.emit(if (guide == null) State.Empty else State.Data(guide)) }
            .catch { error -> _state.emit(State.Error(error)) }
            .launchIn(viewModelScope)
    }
}

package n7.ad2.hero.page.internal.info

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
import n7.ad2.app.logger.Logger
import n7.ad2.hero.page.internal.info.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo

class HeroInfoViewModel @AssistedInject constructor(
    private val getVOHeroDescriptionUseCase: GetVOHeroDescriptionUseCase,
    private val logger: Logger,
    @Assisted private val heroName: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(heroName: String): HeroInfoViewModel
    }

    sealed class State {
        data class Data(val info: VOHeroInfo) : State()
        data class Error(val error: Throwable) : State()
        object Loading : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        logger.log("HeroInfoLoaded", mapOf("Hero" to heroName))
        getVOHeroDescriptionUseCase(heroName)
            .onEach { info -> _state.emit(State.Data(info)) }
            .catch { error -> _state.emit(State.Error(error)) }
            .launchIn(viewModelScope)
    }
}

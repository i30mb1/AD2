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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import n7.ad2.app.logger.Logger
import n7.ad2.hero.page.internal.guides.domain.interactor.GetVOHeroGuideUseCase
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuide
import n7.ad2.hero.page.internal.info.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo

class HeroInfoViewModel @AssistedInject constructor(
    getVOHeroDescriptionUseCase: GetVOHeroDescriptionUseCase,
    getVOHeroGuideUseCase: GetVOHeroGuideUseCase,
    logger: Logger,
    @Assisted heroName: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(heroName: String): HeroInfoViewModel
    }

    sealed class State {
        /** [guide] arrives later from the internet, without it the page simply has no matchups and items. */
        data class Data(val info: VOHeroInfo, val guide: VOGuide?) : State()
        data class Error(val error: Throwable) : State()
        object Loading : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        logger.log("HeroInfoLoaded", mapOf("Hero" to heroName))
        // the page does not wait for the guide: without the internet it simply never comes
        val guide = getVOHeroGuideUseCase(heroName)
            .onStart<VOGuide?> { emit(null) }
            .catch { emit(null) }
        combine(getVOHeroDescriptionUseCase(heroName), guide) { info, loadedGuide -> State.Data(info, loadedGuide) }
            .onEach { _state.emit(it) }
            .catch { error -> _state.emit(State.Error(error)) }
            .launchIn(viewModelScope)
    }
}

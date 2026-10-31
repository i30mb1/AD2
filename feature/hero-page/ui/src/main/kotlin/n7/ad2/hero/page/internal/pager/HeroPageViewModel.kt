package n7.ad2.hero.page.internal.pager

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
import n7.ad2.hero.page.internal.info.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.heroes.domain.model.HeroDescription

/** Data of the banner above the tabs, it stays the same on every tab. Without it the banner shows only the name and art. */
class HeroPageViewModel @AssistedInject constructor(
    getVOHeroDescriptionUseCase: GetVOHeroDescriptionUseCase,
    @Assisted heroName: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(heroName: String): HeroPageViewModel
    }

    private val _description = MutableStateFlow<HeroDescription?>(null)
    val description: StateFlow<HeroDescription?> = _description.asStateFlow()

    init {
        getVOHeroDescriptionUseCase(heroName)
            .onEach { info -> _description.emit(info.description) }
            .catch { }
            .launchIn(viewModelScope)
    }
}

package n7.ad2.heroes.ui.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.ad2.heroes.domain.usecase.UpdateStateViewedForHeroUseCase
import n7.ad2.heroes.ui.internal.domain.usecase.FilterHeroesUseCase
import n7.ad2.heroes.ui.internal.domain.usecase.GetVOHeroesListUseCase
import n7.ad2.heroes.ui.internal.domain.vo.VOHero

internal class HeroesViewModel @AssistedInject constructor(
    getVOHeroesListUseCase: GetVOHeroesListUseCase,
    private val filterHeroesUseCase: Provider<FilterHeroesUseCase>,
    private val updateStateViewedForHeroUseCase: Provider<UpdateStateViewedForHeroUseCase>,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): HeroesViewModel
    }

    val allHeroes: MutableStateFlow<List<VOHero>> = MutableStateFlow(emptyList())
    val _filteredHeroes: MutableStateFlow<List<VOHero>> = MutableStateFlow(emptyList())
    val filteredHeroes: StateFlow<List<VOHero>> = _filteredHeroes.asStateFlow()

    init {
        getVOHeroesListUseCase()
            .onEach(_filteredHeroes::emit)
            .onEach(allHeroes::emit)
            .launchIn(viewModelScope)
    }

    fun filterHeroes(filter: String) {
        allHeroes.map { list -> filterHeroesUseCase.get()(list, filter) }
            .onEach { _filteredHeroes.emit(it) }
    }

    fun updateViewedByUserFieldForHero(name: String) = viewModelScope.launch {
        updateStateViewedForHeroUseCase.get()(name)
    }
}

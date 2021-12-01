package n7.ad2.heroes.internal

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.ad2.heroes.internal.domain.usecase.FilterHeroesUseCase
import n7.ad2.heroes.internal.domain.usecase.GetVOHeroesListUseCase
import n7.ad2.heroes.internal.domain.usecase.UpdateViewedByUserFieldUseCase
import n7.ad2.heroes.internal.domain.vo.VOHero

class HeroesViewModel @AssistedInject constructor(
    private val application: Application,
    getVOHeroesListUseCase: GetVOHeroesListUseCase,
    private val filterHeroesUseCase: FilterHeroesUseCase,
    private val updateViewedByUserFieldUseCase: UpdateViewedByUserFieldUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): HeroesViewModel
    }

    private val allHeroes: MutableStateFlow<List<VOHero>> = MutableStateFlow(emptyList())
    private val _filteredHeroes: MutableStateFlow<List<VOHero>> = MutableStateFlow(emptyList())
    val filteredHeroes: StateFlow<List<VOHero>> = _filteredHeroes.asStateFlow()

    init {
        getVOHeroesListUseCase()
            .onEach(_filteredHeroes::emit)
            .onEach(allHeroes::emit)
            .launchIn(viewModelScope)
    }

    fun filterHeroes(filter: String) {
        allHeroes.map { list -> filterHeroesUseCase(list, filter) }
            .onEach { _filteredHeroes.emit(it) }
    }

    fun updateViewedByUserFieldForHero(name: String) = viewModelScope.launch {
        updateViewedByUserFieldUseCase(name)
    }

}
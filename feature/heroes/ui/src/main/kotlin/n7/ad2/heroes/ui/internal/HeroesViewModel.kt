package n7.ad2.heroes.ui.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import n7.ad2.heroes.domain.usecase.UpdateStateViewedForHeroUseCase
import n7.ad2.heroes.ui.internal.domain.usecase.FilterHeroesUseCase
import n7.ad2.heroes.ui.internal.domain.usecase.GetVOHeroesListUseCase
import n7.ad2.heroes.ui.internal.domain.vo.VOHero

internal class HeroesViewModel @AssistedInject constructor(
    private val getVOHeroesListUseCase: GetVOHeroesListUseCase,
    private val filterHeroesUseCase: Provider<FilterHeroesUseCase>,
    private val updateStateViewedForHeroUseCase: Provider<UpdateStateViewedForHeroUseCase>,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): HeroesViewModel
    }

    private val refresh = MutableSharedFlow<Unit>()

    val allHeroes = MutableStateFlow<List<VOHero>>(emptyList())
    private val _filteredHeroes = MutableStateFlow<List<VOHero>>(emptyList())
    val filteredHeroes: StateFlow<List<VOHero>> = _filteredHeroes

    private val loadJob = flow {
        emitAll(getVOHeroesListUseCase())
        refresh.collect { emitAll(getVOHeroesListUseCase()) }
    }.onEach { heroes ->
        allHeroes.emit(heroes)
        _filteredHeroes.emit(heroes)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun refresh() = viewModelScope.launch { refresh.emit(Unit) }

    fun filterHeroes(filter: String) {
        allHeroes.map { list -> filterHeroesUseCase.get()(list, filter) }
            .onEach { _filteredHeroes.emit(it) }
            .launchIn(viewModelScope)
    }

    fun updateViewedByUserFieldForHero(name: String) = viewModelScope.launch {
        updateStateViewedForHeroUseCase.get()(name)
    }
}

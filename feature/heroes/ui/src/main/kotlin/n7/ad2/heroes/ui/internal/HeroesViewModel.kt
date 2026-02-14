package n7.ad2.heroes.ui.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import n7.ad2.heroes.domain.usecase.UpdateStateViewedForHeroUseCase
import n7.ad2.heroes.ui.internal.domain.usecase.FilterHeroesUseCase
import n7.ad2.heroes.ui.internal.domain.usecase.GetVOHeroesListUseCase
import n7.ad2.heroes.ui.internal.domain.vo.VOHero
import javax.inject.Provider

internal class HeroesViewModel @AssistedInject constructor(
    private val getVOHeroesListUseCase: GetVOHeroesListUseCase,
    private val filterHeroesUseCase: Provider<FilterHeroesUseCase>,
    private val updateStateViewedForHeroUseCase: Provider<UpdateStateViewedForHeroUseCase>,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): HeroesViewModel
    }

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }
    private val filterQuery = MutableStateFlow("")

    val heroes: StateFlow<List<VOHero>> = refreshTrigger
        .flatMapLatest { getVOHeroesListUseCase() }
        .combine(filterQuery) { heroes, query -> Pair(heroes, query) }
        .mapLatest { (heroes, query) ->
            filterHeroesUseCase.get()(heroes, query)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun refresh() = viewModelScope.launch {
        refreshTrigger.emit(Unit)
    }

    fun filterHeroes(filter: String) {
        filterQuery.value = filter
    }

    fun updateViewedByUserFieldForHero(name: String) = viewModelScope.launch {
        updateStateViewedForHeroUseCase.get()(name)
    }
}

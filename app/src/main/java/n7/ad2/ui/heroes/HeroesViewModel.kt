package n7.ad2.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.ad2.ui.heroes.domain.interactor.GetVOHeroesListInteractor
import n7.ad2.ui.heroes.domain.usecase.UpdateViewedByUserFieldUseCase
import n7.ad2.ui.heroes.domain.vo.VOHero
import javax.inject.Inject

class HeroesViewModel @Inject constructor(
    application: Application,
    getVOHeroesListInteractor: GetVOHeroesListInteractor,
    private val updateViewedByUserFieldUseCase: UpdateViewedByUserFieldUseCase,
) : AndroidViewModel(application) {

    private val allHeroes: Flow<List<VOHero>> = getVOHeroesListInteractor()
    private val _filteredHeroes: MutableStateFlow<List<VOHero>> = MutableStateFlow(emptyList())
    val filteredHeroes: StateFlow<List<VOHero>> = _filteredHeroes.asStateFlow()

    init {
        getVOHeroesListInteractor()
            .onEach(_filteredHeroes::emit)
            .launchIn(viewModelScope)
    }

    fun filterHeroes(filter: String) = viewModelScope.launch {
        allHeroes.map { list ->
            list.filter { it.name.contains(filter) }
        }.collect { _filteredHeroes.emit(it) }
    }

    fun updateViewedByUserFieldForHero(name: String) {
        viewModelScope.launch {
            updateViewedByUserFieldUseCase(name)
        }
    }

}
package n7.ad2.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.ad2.ui.heroes.domain.interactor.GetVOHeroesListInteractor
import n7.ad2.ui.heroes.domain.usecase.FilterHeroesUseCase
import n7.ad2.ui.heroes.domain.usecase.UpdateViewedByUserFieldUseCase
import n7.ad2.ui.heroes.domain.vo.VOHero
import javax.inject.Inject

class HeroesViewModel @Inject constructor(
    application: Application,
    getVOHeroesListInteractor: GetVOHeroesListInteractor,
    private val filterHeroesUseCase: FilterHeroesUseCase,
    private val updateViewedByUserFieldUseCase: UpdateViewedByUserFieldUseCase,
) : AndroidViewModel(application) {

    private val allHeroes: MutableStateFlow<List<VOHero>> = MutableStateFlow(emptyList())
    private val _filteredHeroes: MutableStateFlow<List<VOHero>> = MutableStateFlow(emptyList())
    val filteredHeroes: StateFlow<List<VOHero>> = _filteredHeroes.asStateFlow()

    init {
        viewModelScope.launch {
            val heroesList = getVOHeroesListInteractor()
            allHeroes.emit(heroesList)
            _filteredHeroes.emit(heroesList)
        }
    }

    fun filterHeroes(filter: String) = allHeroes
        .map { list -> filterHeroesUseCase(list, filter) }
        .onEach(_filteredHeroes::emit)
        .launchIn(viewModelScope)

    fun updateViewedByUserFieldForHero(name: String) {
        viewModelScope.launch {
            updateViewedByUserFieldUseCase(name)
        }
    }

}
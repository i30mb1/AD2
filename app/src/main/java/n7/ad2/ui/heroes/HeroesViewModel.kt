package n7.ad2.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import n7.ad2.ui.heroes.domain.vo.VOHero
import javax.inject.Inject

class HeroesViewModel @Inject constructor(
    application: Application,
//    getVOHeroesListInteractor: GetVOHeroesListInteractor,
//    private val filterHeroesUseCase: FilterHeroesUseCase,
//    private val updateViewedByUserFieldUseCase: UpdateViewedByUserFieldUseCase,
) : AndroidViewModel(application) {

    private val allHeroes: MutableStateFlow<List<VOHero>> = MutableStateFlow(emptyList())
    private val _filteredHeroes: MutableStateFlow<List<VOHero>> = MutableStateFlow(emptyList())
    val filteredHeroes: StateFlow<List<VOHero>> = _filteredHeroes.asStateFlow()

    init {
//        getVOHeroesListInteractor()
//            .onEach(_filteredHeroes::emit)
//            .onEach(allHeroes::emit)
//            .launchIn(viewModelScope)
    }

    fun filterHeroes(filter: String) {
//        allHeroes.map { list -> filterHeroesUseCase(list, filter) }
//            .onEach { _filteredHeroes.emit(it) }
    }

    fun updateViewedByUserFieldForHero(name: String) = viewModelScope.launch {
//        updateViewedByUserFieldUseCase(name)
    }

}
package n7.ad2.ui.items

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import n7.ad2.ui.items.domain.vo.VOItem
import javax.inject.Inject

class ItemsViewModel @Inject constructor(
    application: Application,
//    getVOItemsUseCase: GetVOItemsUseCase,
//    private val filterItemsUseCase: FilterItemsUseCase,
//    private val updateItemViewedByUserFieldUseCase: UpdateItemViewedByUserFieldUseCase,
) : AndroidViewModel(application) {

    private val allItems: MutableStateFlow<List<VOItem>> = MutableStateFlow(emptyList())
    private val _filteredItems: MutableStateFlow<List<VOItem>> = MutableStateFlow(emptyList())
    val filteredItems: StateFlow<List<VOItem>> = _filteredItems.asStateFlow()

    init {
//        getVOItemsUseCase()
//            .onEach(allItems::emit)
//            .onEach(_filteredItems::emit)
//            .launchIn(viewModelScope)
    }

    fun filterItems(filter: String) = allItems
//        .map { list -> filterItemsUseCase(list, filter) }
//        .onEach(_filteredItems::emit)
        .launchIn(viewModelScope)

    fun updateViewedByUserFieldForItem(name: String) {
        viewModelScope.launch {
//            updateItemViewedByUserFieldUseCase(name)
        }
    }

}
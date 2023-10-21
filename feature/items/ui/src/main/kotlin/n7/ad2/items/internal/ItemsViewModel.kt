package n7.ad2.items.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.ad2.items.domain.usecase.FilterItemsUseCase
import n7.ad2.items.domain.usecase.UpdateItemViewedForItemUseCase
import n7.ad2.items.internal.domain.usecase.GetVOItemsUseCase
import n7.ad2.items.internal.domain.vo.VOItem

internal class ItemsViewModel @AssistedInject constructor(
    getVOItemsUseCase: GetVOItemsUseCase,
    private val filterItemsUseCase: FilterItemsUseCase,
    private val updateItemViewedByUserFieldUseCase: UpdateItemViewedForItemUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): ItemsViewModel
    }

    private val allItems: MutableStateFlow<List<VOItem>> = MutableStateFlow(emptyList())
    private val _filteredItems: MutableStateFlow<List<VOItem>> = MutableStateFlow(emptyList())
    val filteredItems: StateFlow<List<VOItem>> = _filteredItems.asStateFlow()

    init {
        getVOItemsUseCase()
            .onEach(allItems::emit)
            .onEach(_filteredItems::emit)
            .launchIn(viewModelScope)
    }

//    fun filterItems(filter: String) = allItems
//        .map { list -> filterItemsUseCase(list, filter) }
//        .onEach(_filteredItems::emit)
//        .launchIn(viewModelScope)

    fun updateViewedByUserFieldForItem(name: String) {
        viewModelScope.launch {
            updateItemViewedByUserFieldUseCase(name)
        }
    }
}

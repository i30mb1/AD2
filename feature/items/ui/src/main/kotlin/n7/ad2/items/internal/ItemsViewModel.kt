package n7.ad2.items.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import n7.ad2.items.domain.usecase.UpdateItemViewedForItemUseCase
import n7.ad2.items.internal.model.ItemUI
import n7.ad2.items.internal.usecase.FilterItemsUIUseCase
import n7.ad2.items.internal.usecase.GetItemsUIUseCase

internal class ItemsViewModel @AssistedInject constructor(
    getItemsUIUseCase: GetItemsUIUseCase,
    private val filterItemsUIUseCase: FilterItemsUIUseCase,
    private val updateItemViewedByUserFieldUseCase: UpdateItemViewedForItemUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): ItemsViewModel
    }

    private val filterQuery = MutableStateFlow("")
    val query: StateFlow<String> = filterQuery.asStateFlow()

    val filteredItems: StateFlow<List<ItemUI>> = getItemsUIUseCase()
        .combine(filterQuery) { items, query -> Pair(items, query) }
        .mapLatest { (items, query) -> filterItemsUIUseCase(items, query) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun filterItems(filter: String) {
        filterQuery.value = filter
    }

    fun updateViewedByUserFieldForItem(name: String) {
        viewModelScope.launch {
            updateItemViewedByUserFieldUseCase(name)
        }
    }
}

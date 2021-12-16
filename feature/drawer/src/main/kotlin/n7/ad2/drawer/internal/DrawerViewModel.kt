package n7.ad2.drawer.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import n7.ad2.drawer.internal.domain.usecase.GetMenuItemsUseCase
import n7.ad2.drawer.internal.domain.vo.VOMenu

internal class DrawerViewModel @AssistedInject constructor(
    private val getMenuItemsUseCase: GetMenuItemsUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): DrawerViewModel
    }

    val menu: MutableStateFlow<List<VOMenu>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            val items = getMenuItemsUseCase().single()
            menu.value = items
        }
    }

    fun updateMenu(selectedMenu: VOMenu) {
        menu.value = menu.value.map { menu -> menu.copy(isSelected = menu.type == selectedMenu.type) }
    }

}

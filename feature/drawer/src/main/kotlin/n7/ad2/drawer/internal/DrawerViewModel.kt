package n7.ad2.drawer.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.apppreference.Preference
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.domain.usecase.GetMenuInfoUseCase
import n7.ad2.drawer.internal.domain.usecase.GetMenuItemsUseCase
import n7.ad2.drawer.internal.domain.usecase.MenuInfoSource
import n7.ad2.drawer.internal.domain.vo.VOMenu

internal class DrawerViewModel @AssistedInject constructor(
    private val getMenuItemsUseCase: GetMenuItemsUseCase,
    private val getMenuInfoUseCase: GetMenuInfoUseCase,
    private val infoSource: MenuInfoSource,
    private val preferences: Preference,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): DrawerViewModel
    }

    val menu: MutableStateFlow<List<VOMenu>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            menu.value = getMenuItemsUseCase().single()
            getMenuInfoUseCase().collect { (type, info) ->
                menu.update { list -> list.map { item -> if (item.type == type) item.copy(info = info) else item } }
            }
        }
    }

    suspend fun isLogWidgetEnabled(): Boolean = preferences.isLogWidgetEnabled()

    fun updateMenu(selectedMenu: VOMenu) {
        menu.update { list -> list.map { menu -> menu.copy(isSelected = menu.type == selectedMenu.type) } }
        if (selectedMenu.type == VOMenuType.NEWS) markNewsRead()
    }

    private fun markNewsRead() {
        menu.update { list -> list.map { menu -> if (menu.type == VOMenuType.NEWS) menu.copy(info = null) else menu } }
        viewModelScope.launch { infoSource.setNewsSeenAt(System.currentTimeMillis() / 1000) }
    }
}

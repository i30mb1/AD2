package n7.ad2.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import n7.ad2.AD2Logger
import n7.ad2.ui.main.domain.usecase.GetMenuItemsUseCase
import n7.ad2.ui.vo.VOMenu
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getMenuItemsUseCase: GetMenuItemsUseCase,
    private val application: Application,
    private val logger: AD2Logger,
) : ViewModel() {

    val menu: MutableLiveData<List<VOMenu>> = MutableLiveData()

    init {
        viewModelScope.launch {
            val items = getMenuItemsUseCase().single()
            menu.value = items
        }

    }

    fun updateMenu(selectedMenu: VOMenu) {
        menu.value = menu.value?.map { menu -> menu.copy(isSelected = menu.type == selectedMenu.type) }
    }

}

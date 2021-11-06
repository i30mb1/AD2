package n7.ad2.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.single
import n7.ad2.AD2Logger
import n7.ad2.ui.GetMenuItemsUseCase
import n7.ad2.ui.vo.VOMenu
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getMenuItemsUseCase: GetMenuItemsUseCase,
    private val application: Application,
    private val logger: AD2Logger,
) : ViewModel() {

    val menu: LiveData<List<VOMenu>> = liveData {
        emit(getMenuItemsUseCase().single())
    }


}

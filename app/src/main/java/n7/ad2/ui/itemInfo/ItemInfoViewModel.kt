package n7.ad2.ui.itemInfo

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import n7.ad2.data.source.local.Locale
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.itemInfo.domain.interactor.GetVOItemDescriptionInteractor
import javax.inject.Inject

class ItemInfoViewModel @Inject constructor(
    private val application: Application,
    private val getVOItemDescriptionInteractor: GetVOItemDescriptionInteractor,
) : ViewModel() {

    private val _voItemInfo = MutableLiveData<List<VODescription>>()
    val voItemInfo: LiveData<List<VODescription>> = _voItemInfo

    fun loadItemInfo(itemName: String, locale: Locale) = viewModelScope.launch {
        _voItemInfo.value = getVOItemDescriptionInteractor(itemName, locale)
    }

}
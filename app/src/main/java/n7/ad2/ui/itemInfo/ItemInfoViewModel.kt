package n7.ad2.ui.itemInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import n7.ad2.data.source.local.Locale
import n7.ad2.ui.itemInfo.domain.interactor.GetVOItemInfoInteractor
import n7.ad2.ui.itemInfo.domain.vo.ItemInfo
import javax.inject.Inject

class ItemInfoViewModel @Inject constructor(
    private val getVOItemInfoInteractor: GetVOItemInfoInteractor,
) : ViewModel() {

    private val _voItemInfo = MutableLiveData<List<ItemInfo>>()
    val voItemInfo: LiveData<List<ItemInfo>> = _voItemInfo

    fun loadItemInfo(itemName: String, locale: Locale) = viewModelScope.launch {
        _voItemInfo.value = getVOItemInfoInteractor(itemName, locale)!!
    }

}
package n7.ad2.ui.itemInfo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.data.source.local.Locale
import n7.ad2.ui.itemInfo.domain.interactor.GetVOItemInfoInteractor
import n7.ad2.ui.itemInfo.domain.vo.ItemInfo
import n7.ad2.utils.onFailure
import n7.ad2.utils.onSuccess

class ItemInfoViewModel @AssistedInject constructor(
    application: Application,
    private val getVOItemInfoInteractor: GetVOItemInfoInteractor,
    @Assisted private val itemName: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(itemName: String): ItemInfoViewModel
    }

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> = _error
    private val _voItemInfo = MutableLiveData<List<ItemInfo>>()
    val voItemInfo: LiveData<List<ItemInfo>> = _voItemInfo

    init {
        loadItemInfo(itemName, Locale.valueOf(application.getString(R.string.locale)))
    }

    private fun loadItemInfo(itemName: String, locale: Locale) = viewModelScope.launch {
        getVOItemInfoInteractor(itemName, locale)
            .onSuccess {
                _voItemInfo.value = it
            }
            .onFailure {
                _error.value = it
                _error.value = null
            }
    }

}
package n7.ad2.itempage.internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.android.AD2ErrorMessage
import n7.ad2.android.ErrorMessage
import n7.ad2.itempage.internal.domain.usecase.GetItemInfoUseCase
import n7.ad2.itempage.internal.domain.vo.VOItemInfo

class ItemInfoViewModel @AssistedInject constructor(appInfo: AppInformation, private val getItemInfoUseCase: GetItemInfoUseCase, @Assisted private val itemName: String) :
    ViewModel(),
    ErrorMessage by AD2ErrorMessage() {

    @AssistedFactory
    interface Factory {
        fun create(itemName: String): ItemInfoViewModel
    }

    private val _voItemInfo = MutableLiveData<List<VOItemInfo>>()
    val voItemInfo: LiveData<List<VOItemInfo>> = _voItemInfo

    init {
        loadItemInfo(itemName, appInfo.appLocale)
    }

    fun onStartPlayingItem(_soundUrl: String) {
        _voItemInfo.value = checkNotNull(_voItemInfo.value).map { item ->
            if (item is VOItemInfo.Title) item.copy(data = item.data.copy(isPlaying = true)) else item
        }
    }

    fun onEndPlayingItem() {
        _voItemInfo.value = checkNotNull(_voItemInfo.value).map { item ->
            if (item is VOItemInfo.Title) item.copy(data = item.data.copy(isPlaying = false)) else item
        }
    }

    private fun loadItemInfo(itemName: String, appLocale: AppLocale) = getItemInfoUseCase(itemName, appLocale)
//        .catch { error -> showError(error) }
        .onEach { list -> _voItemInfo.value = list }
        .launchIn(viewModelScope)
}

package n7.ad2.item_page.internal

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.android.ErrorMessage
import n7.ad2.android.ErrorMessageDelegate
import n7.ad2.android.Locale
import n7.ad2.item_page.internal.domain.usecase.GetItemInfoUseCase
import n7.ad2.item_page.internal.domain.vo.VOItemInfo

class ItemInfoViewModel @AssistedInject constructor(
    application: Application,
    private val getItemInfoUseCase: GetItemInfoUseCase,
    @Assisted private val itemName: String,
) : ViewModel(), ErrorMessage by ErrorMessageDelegate() {

    @AssistedFactory
    interface Factory {
        fun create(itemName: String): ItemInfoViewModel
    }

    private val _voItemInfo = MutableLiveData<List<VOItemInfo>>()
    val voItemInfo: LiveData<List<VOItemInfo>> = _voItemInfo

    init {
        loadItemInfo(itemName, Locale.valueOf(application.getString(n7.ad2.android.R.string.locale)))
    }

    fun onStartPlayingItem(soundUrl: String) {
        _voItemInfo.value = checkNotNull(_voItemInfo.value).map { item ->
            if (item is VOItemInfo.Title) item.copy(data = item.data.copy(isPlaying = true)) else item
        }
    }

    fun onEndPlayingItem() {
        _voItemInfo.value = checkNotNull(_voItemInfo.value).map { item ->
            if (item is VOItemInfo.Title) item.copy(data = item.data.copy(isPlaying = false)) else item
        }
    }

    private fun loadItemInfo(itemName: String, locale: Locale) = getItemInfoUseCase(itemName, locale)
        .catch { error -> showError(error) }
        .onEach { list -> _voItemInfo.value = list }
        .launchIn(viewModelScope)

}
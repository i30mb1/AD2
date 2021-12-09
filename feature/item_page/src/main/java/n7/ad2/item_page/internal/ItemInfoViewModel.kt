package n7.ad2.item_page.internal

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import n7.ad2.android.Locale
import n7.ad2.item_page.R
import n7.ad2.item_page.internal.domain.interactor.GetItemInfoUseCase
import n7.ad2.item_page.internal.domain.vo.VOItemInfo

class ItemInfoViewModel @AssistedInject constructor(
    application: Application,
    private val getItemInfoUseCase: GetItemInfoUseCase,
    @Assisted private val itemName: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(itemName: String): ItemInfoViewModel
    }

    private val _error = Channel<Throwable>()
    val error = _error.receiveAsFlow()
    private val _voItemInfo = MutableLiveData<List<VOItemInfo>>()
    val voItemInfo: LiveData<List<VOItemInfo>> = _voItemInfo

    init {
        loadItemInfo(itemName, Locale.valueOf(application.getString(R.string.locale)))
    }

    private fun loadItemInfo(itemName: String, locale: Locale) = getItemInfoUseCase(itemName, locale)
        .catch { error -> _error.send(error) }
        .onEach { list -> _voItemInfo.value = list }
        .launchIn(viewModelScope)

}
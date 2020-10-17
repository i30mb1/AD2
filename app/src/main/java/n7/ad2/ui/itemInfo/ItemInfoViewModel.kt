package n7.ad2.ui.itemInfo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import javax.inject.Inject

class ItemInfoViewModel @Inject constructor(
    private val application: Application,
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val _voItemInfo = MutableLiveData<VODescription>()
    val voItemInfo: LiveData<VODescription> = _voItemInfo

    fun loadItemInfo(itemName: String) = viewModelScope.launch {

    }

}
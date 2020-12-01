package n7.ad2.ui.items

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.toLiveData
import kotlinx.coroutines.launch
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.ui.items.domain.adapter.toVOItemBody
import n7.ad2.ui.items.domain.usecase.UpdateItemViewedByUserFieldUseCase
import javax.inject.Inject

class ItemsViewModel @Inject constructor(
    application: Application,
    appDatabase: AppDatabase,
    private val updateItemViewedByUserFieldUseCase: UpdateItemViewedByUserFieldUseCase,
) : AndroidViewModel(application) {

    private val itemsDao = appDatabase.itemsDao
    private val itemsFilter = MutableLiveData("")
    val itemsPagedList = itemsFilter.switchMap {
        itemsDao.getItemsFilter(it).map { it.toVOItemBody() }.toLiveData(20)
    }

    fun updateViewedByUserFieldForItem(name: String) {
        viewModelScope.launch {
            updateItemViewedByUserFieldUseCase(name)
        }
    }

}
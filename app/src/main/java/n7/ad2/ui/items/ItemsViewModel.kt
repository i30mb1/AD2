package n7.ad2.ui.items

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.toLiveData
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.ui.items.domain.adapter.toVO
import javax.inject.Inject

class ItemsViewModel @Inject constructor(
    application: Application,
    appDatabase: AppDatabase
) : AndroidViewModel(application) {

    private val itemsDao = appDatabase.itemsDao
    private val itemsFilter = MutableLiveData("")
    val itemsPagedList = itemsFilter.switchMap {
        itemsDao.getItemsFilter(it).map { it.toVO() }.toLiveData(20)
    }

}
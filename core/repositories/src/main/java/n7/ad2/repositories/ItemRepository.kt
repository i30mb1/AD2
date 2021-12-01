@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.repositories

import android.app.Application
import kotlinx.coroutines.flow.Flow
import n7.ad2.database_guides.api.dao.ItemsDao
import n7.ad2.database_guides.internal.model.LocalItem
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val application: Application,
    private val itemsDao: ItemsDao,
) {

    companion object {
        const val ASSETS_PATH_ITEM_DESC = "description.json"
        fun getFullUrlItemImage(itemName: String) = "file:///android_asset/items/$itemName/full.webp"
    }

    fun getAllItems(): Flow<List<LocalItem>> {
        return itemsDao.getAllItems()
    }

    suspend fun insertItems(list: List<LocalItem>) {
//        appDatabase.itemsDao.insert(list)
    }

    fun getItemDescription(itemName: String, locale: Locale): String {
        return application.assets.open("items/$itemName/${locale.folderName}/$ASSETS_PATH_ITEM_DESC")
            .bufferedReader().use {
                it.readText()
            }
    }

    suspend fun updateItemViewedByUserField(name: String) {
//        appDatabase.itemsDao.updateItemViewedByUserField(name)
    }

}
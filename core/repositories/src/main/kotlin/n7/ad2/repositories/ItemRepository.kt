@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.repositories

import android.app.Application
import kotlinx.coroutines.flow.Flow
import n7.ad2.android.Locale
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
        itemsDao.insert(list)
    }

    fun getItem(itemName: String, locale: Locale): String {
        return application.assets.open("items/$itemName/${locale.folderName}/description.json")
            .bufferedReader().use {
                it.readText()
            }
    }

    suspend fun updateItemViewedByUserField(name: String) {
        itemsDao.updateItemViewedByUserField(name)
    }

}
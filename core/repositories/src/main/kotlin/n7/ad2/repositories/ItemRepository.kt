@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.repositories

import kotlinx.coroutines.flow.Flow
import n7.ad2.AppLocale
import n7.ad2.AppResources
import n7.ad2.database_guides.api.dao.ItemsDao
import n7.ad2.database_guides.internal.model.LocalItem
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val res: AppResources,
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

    fun getItem(itemName: String, appLocale: AppLocale): String {
        return res.getAssets("items/$itemName/${appLocale.value}/description.json")
            .bufferedReader().use {
                it.readText()
            }
    }

    suspend fun updateItemViewedByUserField(name: String) {
        itemsDao.updateItemViewedByUserField(name)
    }

}
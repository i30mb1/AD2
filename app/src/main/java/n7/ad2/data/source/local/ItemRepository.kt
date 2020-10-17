@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.data.source.local

import android.app.Application
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.data.source.local.model.LocalItem
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val application: Application,
    private val appDatabase: AppDatabase
) {

    companion object {
        const val ASSETS_PATH_ITEMS = "items.json"
    }

    suspend fun insertItems(list: List<LocalItem>) {
        appDatabase.itemsDao.insert(list)
    }

    fun getItemDescription(assetsPath: String, locale: Locale): String {
        return application.assets.open("$assetsPath/${locale.folderName}/${Repository.ASSETS_PATH_HERO_DESC}").bufferedReader().use {
            it.readText()
        }
    }

    suspend fun getAssetsItems(): String {
        return application.assets.open(ASSETS_PATH_ITEMS).bufferedReader().use {
            it.readText()
        }
    }

}
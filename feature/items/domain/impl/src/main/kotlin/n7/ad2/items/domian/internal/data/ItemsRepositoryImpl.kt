package n7.ad2.items.domian.internal.data

import android.app.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import n7.ad2.items.domain.model.Item
import n7.ad2.items.domian.internal.ItemsRepository
import n7.ad2.items.domian.internal.data.db.ItemDatabase
import n7.ad2.items.domian.internal.data.db.dao.ItemsDao
import org.json.JSONArray
import org.json.JSONObject

internal class ItemsRepositoryImpl(private val itemsDao: ItemsDao, private val application: Application) : ItemsRepository {

    private val mapper by lazy { ItemDatabaseToItemMapper(loadTitles()) }

    override fun getAllItems(): Flow<List<Item>> = flow {
        addNewItemsFromAssets()
        emitAll(itemsDao.getAllItems().map { localHeroList -> localHeroList.map(mapper) })
    }

    override fun updateItemViewedForUser(name: String) {
        itemsDao.updateItemViewedByUserField(name)
    }

    /** The prepackaged items.db is from 2023; `items/list.json` (core/parser) lists items added since then. Existing rows keep their `viewedByUser` flag. */
    private fun addNewItemsFromAssets() = runCatching {
        val known = itemsDao.getAllNames().toSet()
        val list = JSONArray(readAsset("items/list.json"))
        val new = (0 until list.length()).map(list::getJSONObject)
            .filter { it.getString("name") !in known }
            .map { ItemDatabase(name = it.getString("name"), type = it.getString("type"), viewedByUser = false) }
        if (new.isNotEmpty()) itemsDao.insertAll(new)
    }

    private fun loadTitles(): Map<String, String> = runCatching {
        val json = JSONObject(readAsset("items/names.json"))
        json.keys().asSequence().associateWith { key -> json.getString(key) }
    }.getOrDefault(emptyMap())

    private fun readAsset(path: String): String = application.assets.open(path).bufferedReader().use { it.readText() }
}

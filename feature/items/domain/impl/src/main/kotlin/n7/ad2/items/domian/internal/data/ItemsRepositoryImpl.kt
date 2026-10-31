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
        syncWithAssets()
        emitAll(itemsDao.getAllItems().map { localHeroList -> localHeroList.map(mapper) })
    }

    override fun updateItemViewedForUser(name: String) {
        itemsDao.updateItemViewedByUserField(name)
    }

    /**
     * The prepackaged items.db is from 2023. `items/removed.json` (core/parser) lists items that left the game,
     * `items/list.json` gives the section of items added since then and of items that moved (e.g. into the neutral pool).
     * Existing rows keep their `viewedByUser` flag.
     */
    private fun syncWithAssets() = runCatching {
        val removed = readArray("items/removed.json").let { array -> (0 until array.length()).map(array::getString) }
        if (removed.isNotEmpty()) itemsDao.deleteByNames(removed)

        val known = itemsDao.getAllTypes().associate { it.name to it.type }
        val list = readArray("items/list.json").let { array -> (0 until array.length()).map(array::getJSONObject) }
        val new = mutableListOf<ItemDatabase>()
        for (entry in list) {
            val name = entry.getString("name")
            val type = entry.getString("type")
            when (known[name]) {
                null -> new += ItemDatabase(name = name, type = type, viewedByUser = false)
                type -> Unit
                else -> itemsDao.updateType(name, type)
            }
        }
        if (new.isNotEmpty()) itemsDao.insertAll(new)
    }

    private fun readArray(path: String): JSONArray = runCatching { JSONArray(readAsset(path)) }.getOrDefault(JSONArray())

    private fun loadTitles(): Map<String, String> = runCatching {
        val json = JSONObject(readAsset("items/names.json"))
        json.keys().asSequence().associateWith { key -> json.getString(key) }
    }.getOrDefault(emptyMap())

    private fun readAsset(path: String): String = application.assets.open(path).bufferedReader().use { it.readText() }
}

package n7.ad2.items.domian.internal.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.items.domain.model.Item
import n7.ad2.items.domian.internal.ItemsRepository
import n7.ad2.items.domian.internal.data.db.dao.ItemsDao

internal class ItemsRepositoryImpl(private val itemsDao: ItemsDao) : ItemsRepository {

    override fun getAllItems(): Flow<List<Item>> = itemsDao.getAllItems()
        .map { localHeroList -> localHeroList.map(ItemDatabaseToItemMapper) }

    override fun updateItemViewedForUser(name: String) {
        itemsDao.updateItemViewedByUserField(name)
    }
}

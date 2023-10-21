package n7.ad2.items.domian.internal

import kotlinx.coroutines.flow.Flow
import n7.ad2.items.domain.model.Item


internal interface ItemsRepository {

    fun getAllItems(): Flow<List<Item>>
    fun updateItemViewedForUser(name: String)
}

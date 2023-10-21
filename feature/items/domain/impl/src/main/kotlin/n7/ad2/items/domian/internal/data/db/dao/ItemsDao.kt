package n7.ad2.items.domian.internal.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import n7.ad2.items.domian.internal.data.db.ItemDatabase

@Dao
interface ItemsDao {

    @Query("SELECT * FROM ItemsTable")
    fun getAllItems(): Flow<List<ItemDatabase>>

    @Query("UPDATE ItemsTable SET viewedByUser = 1 WHERE name =:name")
    fun updateItemViewedByUserField(name: String)
}

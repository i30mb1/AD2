package n7.ad2.items.domian.internal.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import n7.ad2.items.domian.internal.data.db.ItemDatabase

data class NameAndType(val name: String, val type: String)

@Dao
interface ItemsDao {

    @Query("SELECT * FROM ItemsTable")
    fun getAllItems(): Flow<List<ItemDatabase>>

    @Query("SELECT name, type FROM ItemsTable")
    fun getAllTypes(): List<NameAndType>

    @Insert
    fun insertAll(items: List<ItemDatabase>)

    @Query("DELETE FROM ItemsTable WHERE name IN (:names)")
    fun deleteByNames(names: List<String>)

    @Query("UPDATE ItemsTable SET type = :type WHERE name = :name")
    fun updateType(name: String, type: String)

    @Query("UPDATE ItemsTable SET viewedByUser = 1 WHERE name =:name")
    fun updateItemViewedByUserField(name: String)
}

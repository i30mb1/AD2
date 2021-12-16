package n7.ad2.database_guides.api.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import n7.ad2.database_guides.internal.model.LocalItem

@Dao
interface ItemsDao : BaseDao<LocalItem> {

    @Query("SELECT * FROM LocalItems")
    fun getAllItems(): Flow<List<LocalItem>>

    @Query("SELECT * FROM LocalItems WHERE name =:name")
    suspend fun getItem(name: String): LocalItem

    @Query("UPDATE LocalItems SET viewedByUser = 1 WHERE name =:name")
    fun updateItemViewedByUserField(name: String)

}
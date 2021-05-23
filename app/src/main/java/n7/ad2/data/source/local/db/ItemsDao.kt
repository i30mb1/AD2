package n7.ad2.data.source.local.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import n7.ad2.base.BaseDao
import n7.ad2.data.source.local.model.LocalItem

@Dao
interface ItemsDao: BaseDao<LocalItem> {

    @Query("SELECT rowid,* FROM LocalItems")
    fun getAllItems(): Flow<List<LocalItem>>

    @Query("SELECT rowid,* FROM LocalItems WHERE name LIKE '%'||:filter||'%'")
    fun getItemsFilter(filter: String): DataSource.Factory<Int, LocalItem>

    @Query("SELECT rowid,* FROM LocalItems WHERE name =:name")
    suspend fun getItem(name: String): LocalItem

    @Query("UPDATE LocalItems SET viewedByUser = 1 WHERE name =:name")
    fun updateItemViewedByUserField(name: String)

}
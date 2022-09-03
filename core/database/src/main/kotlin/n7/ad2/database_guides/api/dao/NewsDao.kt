package n7.ad2.database_guides.api.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import n7.ad2.database_guides.internal.model.NewsLocal

@Dao
interface NewsDao : BaseDao<NewsLocal> {

    @Query("SELECT * FROM NewsLocal")
    fun getAllNews(): Flow<List<NewsLocal>>

}
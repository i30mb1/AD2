package n7.ad2.database_guides.api.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import n7.ad2.database_guides.internal.model.NewsLocal

@Dao
interface NewsDao : BaseDao<NewsLocal> {

    @Query("SELECT * FROM NewsTable")
    fun getNews(): PagingSource<Int, NewsLocal>

    @Query("DELETE FROM NewsTable")
    suspend fun clearAll()

}
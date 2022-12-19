package n7.ad2.database_guides.api.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import n7.ad2.database_guides.internal.model.NewsLocal

@Dao
interface NewsDao : BaseDao<NewsLocal> {

    @Query("SELECT * FROM NewsTable")
    fun getPagingSourceNews(): PagingSource<Int, NewsLocal>

    @Query("SELECT * FROM NewsTable WHERE loadedFromPage =:fromPage")
    suspend fun getNews(fromPage: Int): List<NewsLocal>

    @Query("DELETE FROM NewsTable")
    suspend fun clearAll()

    @Query("SELECT * FROM NewsTable WHERE rowId =:id")
    suspend fun getSingleNews(id: Int): NewsLocal

}
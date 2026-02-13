package n7.ad2.database_guides.api.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import n7.ad2.database_guides.internal.model.NewsDB

@Dao
interface NewsDao : BaseDao<NewsDB> {

    @Query("SELECT * FROM NewsDB")
    fun getPagingSourceNews(): PagingSource<Int, NewsDB>

    @Query("SELECT * FROM NewsDB WHERE loadedFromPage =:fromPage")
    suspend fun getNews(fromPage: Int): List<NewsDB>

    @Query("DELETE FROM NewsDB")
    suspend fun clearAll()

    @Query("SELECT * FROM NewsDB WHERE rowId =:id")
    suspend fun getSingleNews(id: Int): NewsDB
}

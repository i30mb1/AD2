package n7.ad2.database_guides.api.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import n7.ad2.database_guides.internal.model.NewsDB

@Dao
interface NewsDao : BaseDao<NewsDB> {

    @Query("SELECT * FROM NewsDB ORDER BY rowId")
    fun getPagingSourceNews(): PagingSource<Int, NewsDB>

    /** Only Valve's own announcements ([isOfficial] = true) or only the press. */
    @Query("SELECT * FROM NewsDB WHERE isOfficial = :isOfficial ORDER BY rowId")
    fun getPagingSourceNews(isOfficial: Boolean): PagingSource<Int, NewsDB>

    @Query("SELECT * FROM NewsDB WHERE loadedFromPage =:fromPage")
    suspend fun getNews(fromPage: Int): List<NewsDB>

    @Query("DELETE FROM NewsDB")
    suspend fun clearAll()

    @Query("SELECT * FROM NewsDB WHERE rowId =:id")
    suspend fun getSingleNews(id: Int): NewsDB
}

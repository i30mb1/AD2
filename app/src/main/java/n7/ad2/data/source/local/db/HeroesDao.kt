package n7.ad2.data.source.local.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import n7.ad2.base.BaseDao
import n7.ad2.data.source.local.model.LocalHero

@Dao
interface HeroesDao : BaseDao<LocalHero> {

    @Query("SELECT rowid,* FROM LocalHeroes")
    fun getAll(): LiveData<List<LocalHero>>

    @Query("SELECT rowid,* FROM LocalHeroes WHERE name LIKE '%'||:filter||'%'")
    fun getHeroesFilter(filter: String): DataSource.Factory<Int, LocalHero>

    @Query("SELECT rowid,* FROM LocalHeroes WHERE name =:name")
    suspend fun getHero(name: String): LocalHero
}
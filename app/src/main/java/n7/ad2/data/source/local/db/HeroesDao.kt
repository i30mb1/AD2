package n7.ad2.data.source.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import n7.ad2.base.BaseDao
import n7.ad2.data.source.local.model.LocalHero

@Dao
interface HeroesDao : BaseDao<LocalHero> {

    @Query("SELECT * FROM LocalHeroes")
    fun getAll(): LiveData<List<LocalHero>>

}
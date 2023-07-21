package n7.ad2.database_guides.api.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import n7.ad2.database_guides.internal.model.HeroDb
import n7.ad2.database_guides.internal.model.LocalHeroWithGuidesDb

@Dao
interface HeroesDao : BaseDao<HeroDb> {

    @Query("UPDATE LocalHeroes SET viewedByUser = 1 WHERE name =:name")
    fun updateViewedByUserFieldForName(name: String)

    @Transaction
    @Query("SELECT * FROM LocalHeroes WHERE name=:name")
    fun getHeroWithGuides(name: String): Flow<LocalHeroWithGuidesDb>

    @Query("SELECT * FROM LocalHeroes")
    fun getAllHeroes(): Flow<List<HeroDb>>

    @Query("SELECT * FROM LocalHeroes WHERE name =:name")
    fun getHero(name: String): Flow<HeroDb>
}

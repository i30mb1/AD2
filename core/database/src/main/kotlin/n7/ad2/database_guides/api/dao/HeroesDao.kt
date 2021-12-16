package n7.ad2.database_guides.api.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.database_guides.internal.model.LocalHeroWithGuides

@Dao
interface HeroesDao : BaseDao<LocalHero> {

    @Query("UPDATE LocalHeroes SET viewedByUser = 1 WHERE name =:name")
    fun updateViewedByUserFieldForName(name: String)

    @Transaction
    @Query("SELECT * FROM LocalHeroes WHERE name=:name")
    fun getHeroWithGuides(name: String): Flow<LocalHeroWithGuides>

    @Query("SELECT * FROM LocalHeroes")
    fun getAllHeroes(): Flow<List<LocalHero>>

    @Query("SELECT * FROM LocalHeroes WHERE name =:name")
    suspend fun getHero(name: String): LocalHero
}
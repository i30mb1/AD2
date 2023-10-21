package n7.ad2.heroes.domain.internal.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import n7.ad2.heroes.domain.internal.data.db.HeroDatabase

@Dao
interface HeroesDao {

    @Query("UPDATE HeroesTable SET viewedByUser = 1 WHERE name =:name")
    fun updateViewedByUserFieldForName(name: String)

    @Query("SELECT * FROM HeroesTable")
    fun getAllHeroes(): Flow<List<HeroDatabase>>
}

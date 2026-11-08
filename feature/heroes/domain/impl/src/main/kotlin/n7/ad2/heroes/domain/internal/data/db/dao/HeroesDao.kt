package n7.ad2.heroes.domain.internal.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import n7.ad2.heroes.domain.internal.data.db.HeroDatabase

data class NameAndAttribute(val name: String, val mainAttr: String)

@Dao
interface HeroesDao {

    @Query("UPDATE HeroesTable SET viewedByUser = 1 WHERE name =:name")
    fun updateViewedByUserFieldForName(name: String)

    @Query("SELECT * FROM HeroesTable")
    fun getAllHeroes(): Flow<List<HeroDatabase>>

    @Query("SELECT name, mainAttr FROM HeroesTable")
    suspend fun getAllAttributes(): List<NameAndAttribute>

    @Insert
    suspend fun insertAll(heroes: List<HeroDatabase>)

    @Query("UPDATE HeroesTable SET mainAttr = :attribute WHERE name = :name")
    suspend fun updateAttribute(name: String, attribute: String)
}

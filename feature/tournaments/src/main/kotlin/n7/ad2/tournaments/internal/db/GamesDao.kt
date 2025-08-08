package n7.ad2.tournaments.internal.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GamesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun setGames(games: List<TournamentGame>): LongArray

    @Query("DELETE FROM TournamentGame")
    fun deleteAll()

    @Query("DELETE FROM TournamentGame where teamTimeRemains!=0")
    fun deleteAllUnfinished()

    @Query("SELECT * FROM TournamentGame where url=:url")
    fun getGameByUrl(url: String): LiveData<TournamentGame>

    @Query("SELECT * FROM TournamentGame")
    fun getDataSourceGames(): DataSource.Factory<Int, TournamentGame>
}

package n7.ad2.tournaments.db;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface GamesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] setGames(List<TournamentGame> object);

    @Query("DELETE FROM TournamentGame")
    void deleteAll();

    @Query("DELETE FROM TournamentGame where teamTimeRemains!=0")
    void deleteAllUnfinished();

    @Query("SELECT * FROM TournamentGame where url=:url")
    LiveData<TournamentGame> getGameByUrl(String url);

    @Query("SELECT * FROM TournamentGame")
    DataSource.Factory<Integer, TournamentGame> getDataSourceGames();

}

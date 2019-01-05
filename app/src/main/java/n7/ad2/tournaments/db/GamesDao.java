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
    long[] setGames(List<Games> object);

    @Query("DELETE FROM Games")
    void deleteAll();

    @Query("DELETE FROM Games where teamTimeRemains!=0")
    void deleteAllUnfinished();

    @Query("SELECT * FROM Games where url=:url")
    LiveData<Games> getGameByUrl(String url);

    @Query("SELECT * FROM Games")
    DataSource.Factory<Integer, Games> getDataSourceGames();

}

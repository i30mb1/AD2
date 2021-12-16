//package n7.ad2.tournaments.internal.db;
//
//import androidx.lifecycle.LiveData;
//import androidx.paging.DataSource;
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import java.util.List;
//
////@Dao
////public interface GamesDao {
////
////    @Insert(onConflict = OnConflictStrategy.IGNORE)
////    long[] setGames(List<TournamentGame> object);
////
////    @Query("DELETE FROM TournamentGame")
////    void deleteAll();
////
////    @Query("DELETE FROM TournamentGame where teamTimeRemains!=0")
////    void deleteAllUnfinished();
////
////    @Query("SELECT * FROM TournamentGame where url=:url")
////    LiveData<TournamentGame> getGameByUrl(String url);
////
////    @Query("SELECT * FROM TournamentGame")
////    DataSource.Factory<Integer, TournamentGame> getDataSourceGames();
////
////}

package n7.ad2.db.news;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SteamNewsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] setSteamNews(List<SteamNews> object);

    @Query("DELETE FROM SteamNews")
    void deleteAll();

    @Query("SELECT * FROM steamnews")
    DataSource.Factory<Integer, SteamNews> getDataSourceSteamNews();

    @Query("UPDATE SteamNews SET contents=:s where href=:href")
    void setContents(String s, String href);

    @Query("SELECT * FROM SteamNews WHERE href =:href")
    LiveData<SteamNews> getNewsByIndex(String href);
}

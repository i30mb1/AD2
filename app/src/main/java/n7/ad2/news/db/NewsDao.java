package n7.ad2.news.db;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setNews(NewsModel news);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setNews(List<NewsModel> news);

    @Query("DELETE FROM NewsModel")
    void deleteAll();

    @Query("SELECT * FROM NewsModel")
    DataSource.Factory<Integer, NewsModel> getDataSourceNews();

    @Query("UPDATE NewsModel SET content=:s where href=:href")
    void setContent(String s, String href);

    @Query("SELECT * FROM NewsModel WHERE href =:href")
    LiveData<NewsModel> getNewsByIndex(String href);
}

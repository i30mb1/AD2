package n7.ad2.db.heroes;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface HeroesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Heroes hero);

    @Update
    void update(Heroes hero);

    //Integer тип говорит что нужно использовать PositionalDataSource
    @Query("SELECT * FROM heroes ORDER BY name ASC")
    DataSource.Factory<Integer, Heroes> getDataSourceHeroes();

    @Query("SELECT * FROM heroes WHERE codeName LIKE '%'||:s||'%' ORDER BY name ASC")
    DataSource.Factory<Integer, Heroes> getDataSourceHeroesFilter(String s);

    @Query("SELECT * FROM heroes WHERE codeName =:codeName")
    LiveData<Heroes> getHeroByCodeName(String codeName);

    @Query("SELECT * FROM heroes WHERE codeName =:codeName")
    Heroes getHeroByCodeNameObject(String codeName);

    @Query("SELECT * FROM heroes")
    LiveData<List<Heroes>> getHeroes();
}

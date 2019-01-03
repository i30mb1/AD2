package n7.ad2.db.heroes;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface HeroesDao {

    // все эти 3 операции должны выполняется в WorkerThread
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HeroModel hero);
    //будет возвращать id обьекта
//    long insert(HeroModel hero);
//    void insert(HeroModel hero);
    //передача нескольких обьектов
//    void insert(HeroModel... hero);
    //передача списка
//    void insert(List<HeroModel> hero);

    @Update
    void update(HeroModel hero);
    //вернёт колличество обновлённых записей
//    int update(List<HeroModel> hero);

    @Delete
    void delete(HeroModel hero);
    //вернёт колличество удалённых записей
//    int delete(List<HeroModel> hero);

    //Integer тип говорит что нужно использовать PositionalDataSource
    @Query("SELECT * FROM HeroModel ORDER BY name ASC")
    DataSource.Factory<Integer, HeroModel> getDataSourceHeroes();

    @Query("SELECT * FROM HeroModel WHERE codeName LIKE '%'||:s||'%' ORDER BY name ASC")
    DataSource.Factory<Integer, HeroModel> getDataSourceHeroesFilter(String s);

    @Query("SELECT * FROM HeroModel WHERE codeName =:codeName")
    LiveData<HeroModel> getHeroByCodeName(String codeName);

    @Query("SELECT * FROM HeroModel WHERE codeName =:codeName")
    HeroModel getHeroByCodeNameObject(String codeName);

    @Query("SELECT * FROM HeroModel")
    List<HeroModel> getAll();
}

package n7.ad2.heroes.db;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HeroesDao {
    // все 3 (основные операции) должны выполняется в WorkerThread
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HeroModel hero);
    // будет возвращать id обьекта
//    long insert(HeroModel hero);
    // передача нескольких обьектов
//    void insert(HeroModel... hero);
    // передача списка
//    void insert(List<HeroModel> hero);

    @Update
    void update(HeroModel hero);
    // вернёт колличество обновлённых записей
//    int update(List<HeroModel> hero);

    @Delete
    void delete(HeroModel hero);
    // вернёт колличество удалённых записей
//    int delete(List<HeroModel> hero);

    // Integer тип говорит что нужно использовать PositionalDataSource
    @Query("SELECT * FROM HeroModel ORDER BY HeroModel.name ASC")
    DataSource.Factory<Integer, HeroModel> getDataSourceHeroes();

    @Query("SELECT * FROM HeroModel WHERE codeName LIKE '%'||:chars||'%'")
    DataSource.Factory<Integer, HeroModel> getDataSourceHeroesFilter(String chars);
    // in Room 2.1 поиск по совпадению символов
//    @Query("SELECT * FROM HeroModel WHERE codeName MATCH :chars")
//    DataSource.Factory<Integer, HeroModel> getDataSourceHeroesFilter(String chars);

    @Query("SELECT * FROM HeroModel WHERE codeName =:codeName")
    LiveData<HeroModel> getHeroByCodeName(String codeName);

    @Query("SELECT * FROM HeroModel WHERE codeName =:codeName")
    HeroModel getHeroByCodeNameObject(String codeName);

    @Query("UPDATE HeroModel SET guideLastDay=:lastDay  WHERE codeName=:codeName")
    void setGuideLastDay(String codeName, int lastDay);

    @Query("SELECT * FROM HeroModel")
    List<HeroModel> getAll();

    //SQL запросы
    /* SELECT name, Max(killedBy)
    FROM HeroModel
    GROUP BY name - (позволяет определять подмножество значений отдельного поля в терминах другого поля и применять функции агрегирования к полученому подмножеству)для каждой различной позиции num ищем максимальное значение
    ORDER BY 2 DESC
    * */
}

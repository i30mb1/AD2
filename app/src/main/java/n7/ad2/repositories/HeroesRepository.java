package n7.ad2.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import java.util.List;

import n7.ad2.AppExecutors;
import n7.ad2.db.heroes.Heroes;
import n7.ad2.db.heroes.HeroesDao;
import n7.ad2.db.heroes.HeroesRoomDatabase;

public class HeroesRepository {

    private Application application;
    private final AppExecutors appExecutors;
    private final HeroesDao heroesDao;

    public HeroesRepository(Application application) {
        this.application = application;
        appExecutors = new AppExecutors();
        heroesDao = HeroesRoomDatabase.getDatabase(application, appExecutors).heroesDao();
    }

    public LiveData<Heroes> getHero(String name) {
        return heroesDao.getHeroByCodeName(name);
    }

    public LiveData<List<Heroes>> getHeroes() {
        return heroesDao.getHeroes();
    }

    public LiveData<PagedList<Heroes>> getPagedListHeroesFilter(String s) {
        DataSource.Factory dataSource = heroesDao.getDataSourceHeroesFilter(s);
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(20).build();
        LiveData<PagedList<Heroes>> pagedListLiveData = new LivePagedListBuilder<>(dataSource, config).build();
        return pagedListLiveData;
    }

    public LiveData<PagedList<Heroes>> getPagedListHeroes() {
        //DataSource.Factory генерирует сама Room
        DataSource.Factory dataSource = heroesDao.getDataSourceHeroes();
        //PagedList.Config для различных условий загрузки
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(20)
//                .setInitialLoadSizeHint(60) //default = pageSize*3
//                .setPrefetchDistance(10) //default = pageSize сколько записей до конца списка чтобы загрузить новую порцию данных
                .setEnablePlaceholders(true) //default true
                .build();
        //LivePagedListBuilder создаёт PagedList в отдельном потоке (начальную загрузку данных тоже)
        //LivePagedListBuilder используя DataSource.Factory всегда сама может создать новую фабрику
        LiveData<PagedList<Heroes>> pagedListLiveData = new LivePagedListBuilder<>(dataSource, config).build();

        return pagedListLiveData;
    }
}

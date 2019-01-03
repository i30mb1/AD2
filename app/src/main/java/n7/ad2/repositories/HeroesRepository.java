package n7.ad2.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import java.util.List;
import java.util.concurrent.Executor;

import n7.ad2.AppExecutors;
import n7.ad2.db.heroes.HeroModel;
import n7.ad2.db.heroes.HeroesDao;
import n7.ad2.db.heroes.HeroesRoomDatabase;

public class HeroesRepository {

    private final AppExecutors appExecutors;
    private final HeroesDao heroesDao;

    public HeroesRepository(Application application, Executor diskIO) {
        appExecutors = new AppExecutors();
        heroesDao = HeroesRoomDatabase.getDatabase(application, diskIO).heroesDao();
    }

    public LiveData<HeroModel> getHero(String name) {
        return heroesDao.getHeroByCodeName(name);
    }


    public LiveData<PagedList<HeroModel>> getPagedListHeroesFilter(String s) {
        DataSource.Factory dataSource = heroesDao.getDataSourceHeroesFilter(s);
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(20).build();
        LiveData<PagedList<HeroModel>> pagedListLiveData = new LivePagedListBuilder<>(dataSource, config).build();
        return pagedListLiveData;
    }

    public LiveData<PagedList<HeroModel>> getPagedListHeroes() {
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
        LiveData<PagedList<HeroModel>> pagedListLiveData = new LivePagedListBuilder<>(dataSource, config).build();

        return pagedListLiveData;
    }
}

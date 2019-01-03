package n7.ad2.heroes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import n7.ad2.db.heroes.HeroModel;
import n7.ad2.db.heroes.HeroesDao;
import n7.ad2.db.heroes.HeroesRoomDatabase;

public class HeroesViewModel extends AndroidViewModel {

    private Application application;
    private Executor diskIO;
    private LiveData<PagedList<HeroModel>> heroes;

    public LiveData<PagedList<HeroModel>> getHeroes() {
        return heroes;
    }

    public HeroesViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        diskIO = Executors.newSingleThreadExecutor();

        setupLiveDataHeroes();
    }

    public void setupLiveDataHeroes() {
       HeroesDao heroesDao = HeroesRoomDatabase.getDatabase(application, diskIO).heroesDao();

        //DataSource.Factory генерирует сама Room
        DataSource.Factory dataSource = heroesDao.getDataSourceHeroes();

        //PagedList.Config для различных условий загрузки
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(100)
//                .setInitialLoadSizeHint(60) //default = pageSize*3
//                .setPrefetchDistance(10) //default = pageSize сколько записей до конца списка чтобы загрузить новую порцию данных
                .setEnablePlaceholders(true) //default true
                .build();
        //LivePagedListBuilder создаёт PagedList в отдельном потоке (начальную загрузку данных тоже)
        //LivePagedListBuilder используя DataSource.Factory всегда сама может создать новую фабрику при появлении новых данных
        heroes = new LivePagedListBuilder<>(dataSource, config).build();
    }
}

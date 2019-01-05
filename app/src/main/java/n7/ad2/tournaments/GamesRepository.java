package n7.ad2.tournaments;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import n7.ad2.games.GamesWorker;
import n7.ad2.tournaments.db.Games;
import n7.ad2.tournaments.db.GamesDao;
import n7.ad2.tournaments.db.GamesRoomDatabase;

import static n7.ad2.games.GamesWorker.PAGE;
import static n7.ad2.games.GamesWorker.UNIQUE_WORK;

public class GamesRepository {

    private final GamesDao gamesDao;
    private Application application;
    private int page = 0;

    public GamesRepository(Application application) {
        this.application = application;
        gamesDao = GamesRoomDatabase.getDatabase(application).gamesDao();
    }

    public LiveData<PagedList<Games>> getGames() {
        DataSource.Factory factory = gamesDao.getDataSourceGames();
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(10).setEnablePlaceholders(false).build();
        LiveData<PagedList<Games>> listLiveData = new LivePagedListBuilder(factory, config).setBoundaryCallback(new ItemBoundaryCallback()).build();
        return listLiveData;
    }

    public LiveData<Games> getGameByUrl(String url) {
        return gamesDao.getGameByUrl(url);
    }

    private class ItemBoundaryCallback extends PagedList.BoundaryCallback {

        @Override
        public void onItemAtEndLoaded(@NonNull Object itemAtEnd) {
            super.onItemAtEndLoaded(itemAtEnd);
            page = page + 30;
            Data data = new Data.Builder().putInt(PAGE, page).build();
            OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(GamesWorker.class).setInputData(data).build();
            WorkManager.getInstance().beginUniqueWork(UNIQUE_WORK, ExistingWorkPolicy.KEEP, worker).enqueue();
        }
    }
}

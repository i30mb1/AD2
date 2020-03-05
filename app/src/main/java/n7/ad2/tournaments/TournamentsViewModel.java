package n7.ad2.tournaments;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.databinding.ObservableBoolean;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import n7.ad2.tournaments.db.GamesDao;
import n7.ad2.tournaments.db.GamesRoomDatabase;
import n7.ad2.tournaments.db.TournamentGame;

import static n7.ad2.tournaments.TournamentsWorker.PAGE;

public class TournamentsViewModel extends AndroidViewModel {

    private final GamesDao gamesDao;
    public ObservableBoolean isLoading = new ObservableBoolean(false);
    private Application application;
    private int page = 0;

    public TournamentsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

        gamesDao = GamesRoomDatabase.getDatabase(application).gamesDao();
    }

    public LiveData<PagedList<TournamentGame>> getTournamentsGames() {
        DataSource.Factory<Integer, TournamentGame> dataSource = gamesDao.getDataSourceGames();
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(30)
                .setInitialLoadSizeHint(30)
                .setPrefetchDistance(10)
                .setEnablePlaceholders(true).build();
        LiveData<PagedList<TournamentGame>> listLiveData = new LivePagedListBuilder<>(dataSource, config).setBoundaryCallback(new PagedList.BoundaryCallback<TournamentGame>() {
            @Override
            public void onItemAtEndLoaded(@NonNull TournamentGame itemAtEnd) {
                super.onItemAtEndLoaded(itemAtEnd);
                page = page + 30;
                Data data = new Data.Builder().putInt(PAGE, page).build();
                OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(TournamentsWorker.class).setInputData(data).build();
                WorkManager.getInstance().enqueue(worker);
                WorkManager.getInstance().getWorkInfoByIdLiveData(worker.getId()).observeForever(new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        if (workInfo != null) {
                            if (workInfo.getState().isFinished()) {
                                isLoading.set(false);
                            } else {
                                isLoading.set(true);
                            }
                        }
                    }
                });
            }
        }).build();
        return listLiveData;
    }

}

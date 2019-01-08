package n7.ad2.tournaments;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import n7.ad2.tournaments.db.GamesDao;
import n7.ad2.tournaments.db.GamesRoomDatabase;
import n7.ad2.tournaments.db.TournamentGame;

import static n7.ad2.tournaments.TournamentsWorker.PAGE;
import static n7.ad2.tournaments.TournamentsWorker.TAG;

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
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(20).setEnablePlaceholders(false).build();
        LiveData<PagedList<TournamentGame>> listLiveData = new LivePagedListBuilder<>(dataSource, config).setBoundaryCallback(new PagedList.BoundaryCallback<TournamentGame>() {
            @Override
            public void onItemAtEndLoaded(@NonNull TournamentGame itemAtEnd) {
                super.onItemAtEndLoaded(itemAtEnd);
                page = page + 30;
                Data data = new Data.Builder().putInt(PAGE, page).build();
                OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(TournamentsWorker.class).setInputData(data).build();
                WorkManager.getInstance().beginUniqueWork(TAG, ExistingWorkPolicy.KEEP, worker).enqueue();
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

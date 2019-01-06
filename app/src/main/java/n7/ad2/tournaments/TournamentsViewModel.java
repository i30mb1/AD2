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
import androidx.work.State;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import n7.ad2.tournaments.db.TournamentGame;
import n7.ad2.tournaments.db.GamesDao;
import n7.ad2.tournaments.db.GamesRoomDatabase;

import static n7.ad2.tournaments.TournamentsWorker.PAGE;
import static n7.ad2.tournaments.TournamentsWorker.TAG;

public class TournamentsViewModel extends AndroidViewModel {

    private Application application;
    public ObservableBoolean isLoading = new ObservableBoolean(false);
    private final GamesDao gamesDao;
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
                WorkManager.getInstance().getStatusById(worker.getId()).observeForever(new Observer<WorkStatus>() {
                    @Override
                    public void onChanged(@Nullable WorkStatus workStatus) {
                        if (workStatus == null) return;
                        if (workStatus.getState() == State.ENQUEUED) isLoading.set(true);
                        if (workStatus.getState() == State.SUCCEEDED) isLoading.set(false);
                        if (workStatus.getState() == State.FAILED) isLoading.set(false);
                    }
                });
            }
        }).build();
        return listLiveData;
    }

}

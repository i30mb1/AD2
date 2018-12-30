package n7.ad2.repositories;

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
import n7.ad2.db.news.SteamNews;
import n7.ad2.db.news.SteamNewsDao;
import n7.ad2.db.news.SteamNewsRoomDatabase;
import n7.ad2.worker.SteamDbNewsWorker;
import n7.ad2.worker.SteamNewsWorker;

import static n7.ad2.worker.SteamDbNewsWorker.PAGE;
import static n7.ad2.worker.SteamNewsWorker.END_DATE;

public class SteamDbNewsRepository {

    private final SteamNewsDao steamNewsDao;
    private Application application;
    private int page = 1;

    public SteamDbNewsRepository(Application application) {
        this.application = application;
        steamNewsDao = SteamNewsRoomDatabase.getDatabase(application).steamNewsDao();
    }

    public LiveData<PagedList<SteamNews>> getSteamNews() {
        DataSource.Factory factory = steamNewsDao.getDataSourceSteamNews();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build();

        LiveData<PagedList<SteamNews>> listLiveData = new LivePagedListBuilder(factory, config).setBoundaryCallback(new ItemBoundaryCallback()).build();
        return listLiveData;
    }

    private class ItemBoundaryCallback extends PagedList.BoundaryCallback {

        @Override
        public void onItemAtEndLoaded(final @NonNull Object itemAtEnd) {
            super.onItemAtEndLoaded(itemAtEnd);
            page++;
            Data data = new Data.Builder().putInt(PAGE, page).build();
            OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(SteamDbNewsWorker.class).setInputData(data).build();
            WorkManager.getInstance().beginUniqueWork(SteamDbNewsWorker.UNIQUE_WORK, ExistingWorkPolicy.KEEP, worker).enqueue();
        }
    }
}

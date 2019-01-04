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
import n7.ad2.news.db.NewsModel;
import n7.ad2.news.db.NewsDao;
import n7.ad2.news.db.NewsRoomDatabase;
import n7.ad2.news.NewsWorker;

import static n7.ad2.news.NewsWorker.PAGE;

public class SteamDbNewsRepository {

    private final NewsDao steamNewsDao;
    private Application application;
    private int page = 1;

    public SteamDbNewsRepository(Application application) {
        this.application = application;
        steamNewsDao = NewsRoomDatabase.getDatabase(application).steamNewsDao();
    }

    public LiveData<PagedList<NewsModel>> getSteamNews() {
        DataSource.Factory factory = steamNewsDao.getDataSourceNews();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build();

        LiveData<PagedList<NewsModel>> listLiveData = new LivePagedListBuilder(factory, config).setBoundaryCallback(new ItemBoundaryCallback()).build();
        return listLiveData;
    }

    private class ItemBoundaryCallback extends PagedList.BoundaryCallback {

        @Override
        public void onItemAtEndLoaded(final @NonNull Object itemAtEnd) {
            super.onItemAtEndLoaded(itemAtEnd);
            page++;
            Data data = new Data.Builder().putInt(PAGE, page).build();
            OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(NewsWorker.class).setInputData(data).build();
            WorkManager.getInstance().beginUniqueWork(NewsWorker.TAG, ExistingWorkPolicy.APPEND, worker).enqueue();
        }
    }
}

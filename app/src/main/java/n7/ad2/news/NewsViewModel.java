package n7.ad2.news;

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
import n7.ad2.news.db.NewsDao;
import n7.ad2.news.db.NewsModel;
import n7.ad2.news.db.NewsRoomDatabase;

import static n7.ad2.news.NewsWorker.PAGE;

public class NewsViewModel extends AndroidViewModel {

    public ObservableBoolean isLoading = new ObservableBoolean(false);
    private Application application;
    private NewsDao newsDao;
    private LiveData<PagedList<NewsModel>> news;
    private int pageNews = 1;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

        setupLiveDataNews();
    }

    private void setupLiveDataNews() {
        newsDao = NewsRoomDatabase.getDatabase(application).steamNewsDao();

        DataSource.Factory<Integer, NewsModel> dataSource = newsDao.getDataSourceNews();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build();

        news = new LivePagedListBuilder<>(dataSource, config).setBoundaryCallback(new PagedList.BoundaryCallback<NewsModel>() {
            @Override
            public void onItemAtEndLoaded(@NonNull NewsModel itemAtEnd) {
                super.onItemAtEndLoaded(itemAtEnd);
                pageNews++;
                Data data = new Data.Builder().putInt(PAGE, pageNews).build();
                final OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(NewsWorker.class).setInputData(data).build();
                WorkManager.getInstance().beginUniqueWork(NewsWorker.TAG, ExistingWorkPolicy.APPEND, worker).enqueue();
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
    }

    public LiveData<PagedList<NewsModel>> getNews() {
        return news;
    }
}

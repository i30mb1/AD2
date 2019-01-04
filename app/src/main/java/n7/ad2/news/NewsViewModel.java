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

import java.util.List;

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

    private Application application;
    private NewsDao newsDao;
    private LiveData<PagedList<NewsModel>> news;
    private int pageNews = 1;
    public ObservableBoolean isLoading = new ObservableBoolean(true);
    private LiveData<List<WorkStatus>> status;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

        setupLiveDataNews();
        setupWorkManagerListener();
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
                OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(NewsWorker.class)
                        .setInputData(data)
                        //                .setInitialDelay(10, TimeUnit.MINUTES)
                        .build();
                WorkManager.getInstance().beginUniqueWork(NewsWorker.TAG, ExistingWorkPolicy.APPEND, worker).enqueue();
            }
        }).build();
    }

    private void setupWorkManagerListener() {
        status = WorkManager.getInstance().getStatusesForUniqueWork(NewsWorker.TAG);
        status.observeForever(new Observer<List<WorkStatus>>() {
            @Override
            public void onChanged(@Nullable List<WorkStatus> workStatuses) {
                if (workStatuses != null) {
                    for (WorkStatus workStatus : workStatuses) {
                        if (workStatus.getState() == State.RUNNING) {
                            isLoading.set(true);
                        }
                    }
                }
            }
        });
    }

    public LiveData<PagedList<NewsModel>> getNews() {
        return news;
    }
}

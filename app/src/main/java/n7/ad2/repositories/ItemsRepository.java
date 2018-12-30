package n7.ad2.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import java.util.List;

import n7.ad2.AppExecutors;
import n7.ad2.db.items.Items;
import n7.ad2.db.items.ItemsDao;
import n7.ad2.db.items.ItemsRoomDatabase;

public class ItemsRepository {

    private final ItemsDao itemsDao;
    private final AppExecutors appExecutors;
    private Application application;

    public ItemsRepository(Application application) {
        this.application = application;
        appExecutors = new AppExecutors();
        itemsDao = ItemsRoomDatabase.getDatabase(application, appExecutors).itemsDao();
    }

    public LiveData<List<Items>> getItems() {
        return itemsDao.getItems();
    }

    public LiveData<PagedList<Items>> getPagedListItemsFilter(String s) {
        DataSource.Factory dataSource = itemsDao.getDataSourceItemsFilter(s);
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(25).build();
        LiveData<PagedList<Items>> pagedListLiveData = new LivePagedListBuilder<>(dataSource, config).build();
        return pagedListLiveData;
    }

    public LiveData<PagedList<Items>> getPagedListItems() {
        DataSource.Factory dataSource = itemsDao.getDataSourceItems();
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(25).setEnablePlaceholders(true).build();
        LiveData<PagedList<Items>> pagedListLiveData = new LivePagedListBuilder<>(dataSource, config).build();
        return pagedListLiveData;
    }
}

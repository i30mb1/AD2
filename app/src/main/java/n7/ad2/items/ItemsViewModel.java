package n7.ad2.items;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import n7.ad2.items.db.ItemModel;
import n7.ad2.items.db.ItemsDao;
import n7.ad2.items.db.ItemsRoomDatabase;

public class ItemsViewModel extends AndroidViewModel {

    private Application application;
    private Executor diskIO;
    private ItemsDao itemsDao;
    private LiveData<PagedList<ItemModel>> items;

    public ItemsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        diskIO = Executors.newSingleThreadExecutor();

        setupLiveDataItems();
    }

    public LiveData<PagedList<ItemModel>> getItems() {
        return items;
    }

    private void setupLiveDataItems() {
        itemsDao = ItemsRoomDatabase.getDatabase(application, diskIO).itemsDao();

        DataSource.Factory<Integer, ItemModel> dataSource = itemsDao.getDataSourceItems();

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(20)
                .build();

        items = new LivePagedListBuilder<>(dataSource, config).build();
    }

    LiveData<PagedList<ItemModel>> getItemsByFilter(String chars) {
        DataSource.Factory<Integer, ItemModel> dataSource = itemsDao.getDataSourceItemsFilter(chars);
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(20).build();
        LiveData<PagedList<ItemModel>> items = new LivePagedListBuilder<>(dataSource, config).build();
        return items;
    }


}

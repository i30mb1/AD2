package n7.ad2.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import java.util.List;

import n7.ad2.db.items.ItemModel;
import n7.ad2.repositories.ItemsRepository;

public class ItemsViewModel extends AndroidViewModel {

    private final ItemsRepository itemsRepository;

    public ItemsViewModel(@NonNull Application application) {
        super(application);
        itemsRepository = new ItemsRepository(application);
    }

//    public LiveData<List<ItemModel>> getItems() {
//        return itemsRepository.getItems();
//    }

    public LiveData<PagedList<ItemModel>> getPagedListFilter(String s) {
        return itemsRepository.getPagedListItemsFilter(s);
    }

}

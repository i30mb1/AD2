package n7.ad2.db.items;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ItemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Items items);

    @Query("SELECT * FROM items ORDER BY name ASC")
    DataSource.Factory<Integer, Items> getDataSourceItems();

    @Query("SELECT * FROM items WHERE codeName LIKE '%'||:s||'%' ORDER BY name ASC")
    DataSource.Factory<Integer, Items> getDataSourceItemsFilter(String s);

    @Query("SELECT * FROM items")
    LiveData<List<Items>> getItems();
}

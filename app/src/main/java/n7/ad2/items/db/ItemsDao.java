package n7.ad2.items.db;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ItemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemModel items);

    @Query("SELECT * FROM ItemModel ORDER BY name ASC")
    DataSource.Factory<Integer, ItemModel> getDataSourceItems();

    @Query("SELECT * FROM ItemModel WHERE codeName LIKE '%'||:chars||'%' ORDER BY name ASC")
    DataSource.Factory<Integer, ItemModel> getDataSourceItemsFilter(String chars);

    @Query("SELECT * FROM ItemModel")
    List<ItemModel> getAll();

}

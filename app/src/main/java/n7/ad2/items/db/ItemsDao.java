package n7.ad2.items.db;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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

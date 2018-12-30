package n7.ad2.db.n7message;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface N7MessageDao {

    //вернёт только первую запись
    @Query("select * from N7Message")
    LiveData<N7Message> getN7Message();

    //заменит запись если ключи одинаковые
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long setMessage(N7Message object);

    //не создаст новую запись а только обновит старую
    @Query("UPDATE N7Message SET message=:s where id=7")
    void setMessage(String s);

    //вернёт запись под именем message
    @Query("SELECT message from N7Message")
    LiveData<String> getMessage();
}

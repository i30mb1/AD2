package n7.ad2.db.n7message;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class N7Message {

    @PrimaryKey
    public int id;

    public String message;
}

package n7.ad2.main.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class N7Message {

    @PrimaryKey
    public int id;

    public String message;
}

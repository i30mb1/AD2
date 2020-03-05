package n7.ad2.items.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class ItemModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String codeName;
    @NonNull
    private String name;

    public ItemModel(@NonNull String codeName, @NonNull String name) {
        this.codeName = codeName;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(@NonNull String codeName) {
        this.codeName = codeName;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

}

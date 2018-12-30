package n7.ad2.db.news;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class SteamNews {


//    public int index;

    public long date;

    public String contents;

    public String title;

    @PrimaryKey
    @NonNull
    public String href;

    public String imageHref;

}

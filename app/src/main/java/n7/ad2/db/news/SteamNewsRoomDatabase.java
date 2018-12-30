package n7.ad2.db.news;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {SteamNews.class}, version = 2)
public abstract class SteamNewsRoomDatabase extends RoomDatabase {

    public static SteamNewsRoomDatabase INSTANCE;

    public static SteamNewsRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SteamNewsRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, SteamNewsRoomDatabase.class, "steamNews2.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract SteamNewsDao steamNewsDao();

}

package n7.ad2.db.n7message;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {N7Message.class}, version = 1)
public abstract class N7MessageRoomDatabase extends RoomDatabase {

    public abstract N7MessageDao n7MessageDao();

    public static N7MessageRoomDatabase INSTANCE;

    public static N7MessageRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (N7MessageRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(), N7MessageRoomDatabase.class, "N7.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

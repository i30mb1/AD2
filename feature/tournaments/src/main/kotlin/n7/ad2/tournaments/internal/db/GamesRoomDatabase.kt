//package n7.ad2.tournaments.internal.db;
//
//import android.content.Context;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//
//@Database(entities = {TournamentGame.class}, version = 2, exportSchema = false)
//public abstract class GamesRoomDatabase extends RoomDatabase {
//
//    public static GamesRoomDatabase INSTANCE;
//
//    public static GamesRoomDatabase getDatabase(final Context context) {
//        if (INSTANCE == null) {
//            synchronized (GamesRoomDatabase.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(context, GamesRoomDatabase.class, "tournaments2.db")
//                            .fallbackToDestructiveMigration()
//                            .build();
//                }
//            }
//        }
//        return INSTANCE;
//    }
//
//    public abstract GamesDao gamesDao();
//}

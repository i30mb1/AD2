package n7.ad2.tournaments.internal.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TournamentGame::class], version = 2, exportSchema = false)
abstract class GamesRoomDatabase : RoomDatabase() {

    abstract fun gamesDao(): GamesDao

    companion object {
        @Volatile
        private var INSTANCE: GamesRoomDatabase? = null

        fun getDatabase(context: Context): GamesRoomDatabase = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                GamesRoomDatabase::class.java,
                "tournaments2.db",
            )
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}

package n7.ad2.heroes.domain.internal.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import n7.ad2.heroes.domain.internal.data.db.dao.HeroesDao

private const val DB_VERSION = 1
private const val DB_NAME = "HeroesDatabase_$DB_VERSION.db"

@Database(
    entities = [
        HeroDatabase::class,
    ],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class HeroesDatabase : RoomDatabase() {

    abstract val heroesDao: HeroesDao

    companion object {
        @Volatile private var INSTANCE: HeroesDatabase? = null

        fun getInstance(context: Context): HeroesDatabase {
            return INSTANCE ?: synchronized(this) {
                return INSTANCE ?: buildDatabase(context).also(::set)
            }
        }

        private fun set(database: HeroesDatabase) {
            INSTANCE = database
        }

        private fun buildDatabase(context: Context): HeroesDatabase {
            return Room.databaseBuilder(
                context,
                HeroesDatabase::class.java,
                DB_NAME,
            )
                .createFromAsset("heroes.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

package n7.ad2.items.domian.internal.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import n7.ad2.items.domian.internal.data.db.dao.ItemsDao

private const val DB_VERSION = 1
private const val DB_NAME = "ItemsDatabase_$DB_VERSION.db"

@Database(
    entities = [
        ItemDatabase::class,
    ],
    version = DB_VERSION,
    exportSchema = false,
)
/**
 * Создаем базу данных из готового items.db файла
 * @see <a href="https://www.youtube.com/watch?v=pe28WeQ0VCc">video tutorial</>
 */
abstract class ItemsDatabase : RoomDatabase() {

    abstract val itemsDao: ItemsDao

    companion object {
        @Volatile private var INSTANCE: ItemsDatabase? = null

        fun getInstance(context: Context): ItemsDatabase {
            return INSTANCE ?: synchronized(this) {
                return INSTANCE ?: buildDatabase(context).also(::set)
            }
        }

        private fun set(database: ItemsDatabase) {
            INSTANCE = database
        }

        private fun buildDatabase(context: Context): ItemsDatabase = Room.databaseBuilder(
            context,
            ItemsDatabase::class.java,
            DB_NAME,
        )
            .createFromAsset("items.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}

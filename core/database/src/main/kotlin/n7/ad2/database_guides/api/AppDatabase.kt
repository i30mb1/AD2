package n7.ad2.database_guides.api

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import n7.ad2.database_guides.api.dao.GuidesDao
import n7.ad2.database_guides.api.dao.NewsDao
import n7.ad2.database_guides.internal.model.LocalGuide
import n7.ad2.database_guides.internal.model.NewsDB

private const val DB_VERSION = 2

@Database(
    entities = [
        LocalGuide::class,
        NewsDB::class,
    ],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract val guidesDao: GuidesDao
    abstract val newsDao: NewsDao

    companion object {
        // the file keeps its first name: a name per version would start an empty database and drop the cached guides
        const val DB_NAME = "AD2Database_1.db"

        /** Version 2: news remember when and where they were published. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE NewsDB ADD COLUMN date INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE NewsDB ADD COLUMN source TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE NewsDB ADD COLUMN author TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE NewsDB ADD COLUMN isOfficial INTEGER NOT NULL DEFAULT 0")
                // the news are only a cache of the Steam feed, reloading them fills the new columns
                db.execSQL("DELETE FROM NewsDB")
            }
        }
    }
}

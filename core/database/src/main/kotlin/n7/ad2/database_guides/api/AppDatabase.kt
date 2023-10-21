package n7.ad2.database_guides.api

import androidx.room.Database
import androidx.room.RoomDatabase
import n7.ad2.database_guides.api.dao.GuidesDao
import n7.ad2.database_guides.api.dao.ItemsDao
import n7.ad2.database_guides.api.dao.NewsDao
import n7.ad2.database_guides.internal.model.LocalGuide
import n7.ad2.database_guides.internal.model.LocalItem
import n7.ad2.database_guides.internal.model.NewsDB

private const val DB_VERSION = 1

@Database(
    entities = [
        LocalItem::class,
        LocalGuide::class,
        NewsDB::class,
    ],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract val itemsDao: ItemsDao
    abstract val guidesDao: GuidesDao
    abstract val newsDao: NewsDao

    companion object {
        const val DB_NAME = "AD2Database_$DB_VERSION.db"
    }

}

package n7.ad2.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import n7.ad2.data.source.local.model.LocalItem
import n7.ad2.database_guides.GuidesDaoProvider
import n7.ad2.database_guides.model.LocalGuide
import n7.ad2.database_heroes.api.HeroesDaoProvider
import n7.ad2.database_heroes.api.model.LocalHero

private const val DB_VERSION = 1

@Database(
    entities = [
        LocalHero::class,
        LocalItem::class,
        LocalGuide::class
    ],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase(), HeroesDaoProvider, GuidesDaoProvider {

    abstract val itemsDao: ItemsDao

    companion object {
        const val DB_NAME = "AppDatabase$DB_VERSION.db"
    }

}
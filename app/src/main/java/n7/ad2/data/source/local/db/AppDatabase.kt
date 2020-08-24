package n7.ad2.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import n7.ad2.data.source.local.model.LocalGuide
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.data.source.local.model.LocalItem

private const val DB_VERSION = 1

@Database(
    entities = [
        LocalHero::class,
        LocalItem::class,
        LocalGuide::class
    ], version = DB_VERSION, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract val heroesDao: HeroesDao
    abstract val guideDao: GuideDao
    abstract val itemsDao: ItemsDao

    companion object {
        const val DB_NAME = "AppDatabase$DB_VERSION.db"
    }

}
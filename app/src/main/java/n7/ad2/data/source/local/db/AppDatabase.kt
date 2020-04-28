package n7.ad2.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import n7.ad2.data.source.local.model.LocalHero

private const val DB_VERSION = 1

@Database(entities = [LocalHero::class], version = DB_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val heroesDao: HeroesDao

    companion object {
        const val DB_NAME = "AppDatabase$DB_VERSION.db"
    }

}
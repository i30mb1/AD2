package n7.ad2.database_guides.api

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import n7.ad2.dagger.ApplicationScope
import n7.ad2.database_guides.api.dao.GuidesDao
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.api.dao.ItemsDao
import n7.ad2.database_guides.internal.worker.DatabaseWorker
import javax.inject.Qualifier

@Qualifier
private annotation class InternalApi

@Module
class DatabaseModule {

    @InternalApi
    @ApplicationScope
    @Provides
    fun provideDatabase(application: Application): AppDatabase {
        fun fillInDatabase() {
            val request = OneTimeWorkRequestBuilder<DatabaseWorker>().build()
            WorkManager.getInstance(application).enqueue(request)
        }
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            AppDatabase.DB_NAME
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    fillInDatabase()
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideItemsDao(@InternalApi database: AppDatabase): ItemsDao = database.itemsDao

    @Provides
    fun provideHeroesDao(@InternalApi database: AppDatabase): GuidesDao = database.guidesDao

    @Provides
    fun provideGuidesDao(@InternalApi database: AppDatabase): HeroesDao = database.heroesDao

}
package n7.ad2.database_guides.api

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import n7.ad2.dagger.ApplicationScope
import n7.ad2.database_guides.api.dao.GuidesDao
import n7.ad2.database_guides.api.dao.ItemsDao
import n7.ad2.database_guides.api.dao.NewsDao
import n7.ad2.database_guides.internal.worker.DatabaseWorker

@dagger.Module
class DatabaseModule {

    @ApplicationScope
    @dagger.Provides
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

    @dagger.Provides
    fun provideItemsDao(database: AppDatabase): ItemsDao = database.itemsDao

    @dagger.Provides
    fun provideHeroesDao(database: AppDatabase): GuidesDao = database.guidesDao

    @dagger.Provides
    fun provideNewsDao(database: AppDatabase): NewsDao = database.newsDao

}

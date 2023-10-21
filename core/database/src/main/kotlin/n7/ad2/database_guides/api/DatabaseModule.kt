package n7.ad2.database_guides.api

import android.app.Application
import androidx.room.Room
import n7.ad2.dagger.ApplicationScope
import n7.ad2.database_guides.api.dao.GuidesDao
import n7.ad2.database_guides.api.dao.NewsDao

@dagger.Module
class DatabaseModule {

    @ApplicationScope
    @dagger.Provides
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            AppDatabase.DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @dagger.Provides
    fun provideHeroesDao(database: AppDatabase): GuidesDao = database.guidesDao

    @dagger.Provides
    fun provideNewsDao(database: AppDatabase): NewsDao = database.newsDao

}

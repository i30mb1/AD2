package n7.ad2.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.workers.DatabaseWorker
import java.util.*

@Module
object ApplicationModule {

    @Reusable
    @Provides
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                AppDatabase.DB_NAME
        )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        fillInDb(application)
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
    }

    private fun fillInDb(application: Application) {
        val request = OneTimeWorkRequestBuilder<DatabaseWorker>().build()
        WorkManager.getInstance(application).enqueue(request)
    }

    @Reusable
    @Provides
    fun moshi(): Moshi = Moshi.Builder()
            .build()

    @Reusable
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Reusable
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Reusable
    @Provides
    fun provideCalendar(): Calendar = Calendar.getInstance()

}

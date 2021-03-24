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
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.workers.DatabaseWorker
import java.util.Calendar

@Module
object ApplicationModule {

    @Reusable
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

    @Reusable
    @Provides
    fun moshi(): Moshi = Moshi.Builder().build()

    @Reusable
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    @Provides
    fun provideCalendar(): Calendar = Calendar.getInstance()

}

package n7.ad2.di

import android.app.Application
import android.content.res.AssetManager
import androidx.work.WorkManager
import dagger.Provides
import dagger.Reusable
import n7.ad2.provider.AD2Provider
import n7.ad2.provider.Provider
import java.util.Calendar
import javax.inject.Singleton

@dagger.Module
class ApplicationModule {

    @Reusable
    @Provides
    fun provideProvider(): Provider = AD2Provider

    @Singleton
    @Provides
    fun provideWorkManager(application: Application): WorkManager = WorkManager.getInstance(application)

    @Singleton
    @Provides
    fun provideCalendar(): Calendar = Calendar.getInstance()

    @Singleton
    @Provides
    fun provideResource(application: Application): AssetManager = application.assets

}
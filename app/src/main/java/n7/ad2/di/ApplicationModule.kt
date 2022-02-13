package n7.ad2.di

import android.app.Application
import android.content.res.AssetManager
import androidx.work.WorkManager
import dagger.Provides
import n7.ad2.dagger.ApplicationScope
import n7.ad2.provider.AD2Provider
import n7.ad2.provider.Provider
import java.util.Calendar

@dagger.Module
class ApplicationModule {

    @Provides
    fun provideProvider(): Provider = AD2Provider

    @ApplicationScope
    @Provides
    fun provideWorkManager(application: Application): WorkManager = WorkManager.getInstance(application)

    @ApplicationScope
    @Provides
    fun provideCalendar(): Calendar = Calendar.getInstance()

    @ApplicationScope
    @Provides
    fun provideResource(application: Application): AssetManager = application.assets

}
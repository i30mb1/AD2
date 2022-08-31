package n7.ad2.di

import android.app.Application
import androidx.work.WorkManager
import dagger.Binds
import dagger.Provides
import n7.ad2.AD2AppInformation
import n7.ad2.AD2AppResources
import n7.ad2.AD2Provider
import n7.ad2.AppInformation
import n7.ad2.AppResources
import n7.ad2.dagger.ApplicationScope
import n7.ad2.provider.Provider
import java.util.Calendar

@dagger.Module
interface ApplicationModule {

    @ApplicationScope
    @Binds
    fun provideAppInfo(aD2AppInformation: AD2AppInformation): AppInformation

    @ApplicationScope
    @Binds
    fun provideAppResource(appResources: AD2AppResources): AppResources

    companion object {

        @Provides
        fun provideProvider(): Provider = AD2Provider

        @ApplicationScope
        @Provides
        fun provideWorkManager(application: Application): WorkManager = WorkManager.getInstance(application)

        @ApplicationScope
        @Provides
        fun provideCalendar(): Calendar = Calendar.getInstance()

    }

}
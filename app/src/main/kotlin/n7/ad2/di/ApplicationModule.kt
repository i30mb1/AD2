package n7.ad2.di

import android.app.Application
import androidx.work.WorkManager
import dagger.Binds
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.ElementsIntoSet
import n7.ad2.AD2AppInformation
import n7.ad2.AD2Provider
import n7.ad2.AD2Resources
import n7.ad2.AD2Settings
import n7.ad2.AppInformation
import n7.ad2.AppSettings
import n7.ad2.Resources
import n7.ad2.dagger.ApplicationScope
import n7.ad2.init.CrashHandlerInitializer
import n7.ad2.init.DevicePerformanceInitializer
import n7.ad2.init.HistoricalProcessExitReasonsInitializer
import n7.ad2.init.Initializer
import n7.ad2.init.StrictModeInitializer
import n7.ad2.init.SystemInfoInitializer
import n7.ad2.init.YandexMetricsInitializer
import n7.ad2.logger.AppLogger
import n7.ad2.logger.Logger
import n7.ad2.provider.Provider
import yandexmetrics.YandexMetricsInit
import java.util.Calendar

@dagger.Module
interface ApplicationModule {

    @Reusable
    @Binds
    fun provideAppInfo(appInformation: AD2AppInformation): AppInformation

    @Reusable
    @Binds
    fun provideAppResource(appResources: AD2Resources): Resources

    @Reusable
    @Binds
    fun provideAppSettings(appSettings: AD2Settings): AppSettings

    @ApplicationScope
    @Binds
    fun provideLogger(logger: AppLogger): Logger

    companion object {

        @Provides
        fun provideProvider(): Provider = AD2Provider

        @Provides
        fun provideWorkManager(application: Application): WorkManager = WorkManager.getInstance(application)

        @ApplicationScope
        @Provides
        fun provideCalendar(): Calendar = Calendar.getInstance()

        @ElementsIntoSet
        @Provides
        fun provideInitializers(yandexMetricsInit: YandexMetricsInit): Set<Initializer> {
            return setOf(
                SystemInfoInitializer(),
                CrashHandlerInitializer(),
                HistoricalProcessExitReasonsInitializer(),
                StrictModeInitializer(),
                DevicePerformanceInitializer(),
                YandexMetricsInitializer(yandexMetricsInit),
            )
        }

    }

}
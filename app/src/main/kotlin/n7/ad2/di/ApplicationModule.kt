package n7.ad2.di

import android.app.Application
import androidx.work.WorkManager
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
import n7.ad2.logger.Logger
import n7.ad2.provider.Provider
import yandex.metrics.YandexMetrics
import yandex.metrics.YandexMetricsInit
import java.util.Calendar

@dagger.Module
interface ApplicationModule {

    @dagger.Reusable
    @dagger.Binds
    fun provideAppInfo(appInformation: AD2AppInformation): AppInformation

    @dagger.Reusable
    @dagger.Binds
    fun provideAppResource(appResources: AD2Resources): Resources

    @dagger.Reusable
    @dagger.Binds
    fun provideAppSettings(appSettings: AD2Settings): AppSettings

    companion object {

        @dagger.Provides
        fun provideProvider(): Provider = AD2Provider

        @dagger.Provides
        fun provideWorkManager(application: Application): WorkManager = WorkManager.getInstance(application)

        @ApplicationScope
        @dagger.Provides
        fun provideLogger(): Logger = Logger(YandexMetrics())

        @ApplicationScope
        @dagger.Provides
        fun provideCalendar(): Calendar = Calendar.getInstance()

        @ElementsIntoSet
        @dagger.Provides
        fun provideInitializers(appInformation: AppInformation): Set<Initializer> {
            return setOf(
                SystemInfoInitializer(),
                CrashHandlerInitializer(),
                HistoricalProcessExitReasonsInitializer(),
                StrictModeInitializer(),
                DevicePerformanceInitializer(),
                YandexMetricsInitializer(YandexMetricsInit(appInformation)),
            )
        }

    }

}
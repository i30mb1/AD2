package n7.ad2.di

import android.app.Application
import androidx.work.WorkManager
import dagger.multibindings.ElementsIntoSet
import n7.ad2.AD2Navigator
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.common.application.BaseApplicationModule
import n7.ad2.common.application.Resources
import n7.ad2.dagger.ApplicationScope
import n7.ad2.init.CrashHandlerInitializer
import n7.ad2.init.DevicePerformanceInitializer
import n7.ad2.init.HistoricalProcessExitReasonsInitializer
import n7.ad2.init.Initializer
import n7.ad2.init.StrictModeInitializer
import n7.ad2.init.SystemInfoInitializer
import n7.ad2.init.YandexMetricsInitializer
import n7.ad2.navigator.Navigator
import yandex.metrics.YandexMetrics
import java.util.Calendar

@dagger.Module(
    includes = [BaseApplicationModule::class],
)
interface ApplicationModule {

    companion object {

        @dagger.Reusable
        @dagger.Provides
        fun provideAppInfo(resources: Resources): AppInformation = AppInformation(resources)

        @dagger.Reusable
        @dagger.Provides
        fun provideProvider(): Navigator = AD2Navigator

        @dagger.Reusable
        @dagger.Provides
        fun provideWorkManager(application: Application): WorkManager = WorkManager.getInstance(application)

        @dagger.Reusable
        @dagger.Provides
        fun provideCalendar(): Calendar = Calendar.getInstance()

        @ApplicationScope
        @dagger.Provides
        fun provideLogger(): Logger = Logger(listOf(YandexMetrics()))

        @ElementsIntoSet
        @dagger.Provides
        fun provideInitializers(): Set<Initializer> = setOf(
            YandexMetricsInitializer(),
            SystemInfoInitializer(),
            CrashHandlerInitializer(),
            HistoricalProcessExitReasonsInitializer(),
            StrictModeInitializer(),
            DevicePerformanceInitializer(),
        )
    }
}

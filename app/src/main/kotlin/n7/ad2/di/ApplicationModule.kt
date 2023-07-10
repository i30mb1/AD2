package n7.ad2.di

import android.app.Application
import androidx.work.WorkManager
import dagger.multibindings.ElementsIntoSet
import n7.ad2.AD2AppInformation
import n7.ad2.AD2Navigator
import n7.ad2.AppInformation
import n7.ad2.app.logger.Logger
import n7.ad2.common.application.BaseApplicationModule
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
import yandex.metrics.YandexMetricsInit
import java.util.Calendar

@dagger.Module(
    includes = [BaseApplicationModule::class]
)
interface ApplicationModule {

    @dagger.Reusable
    @dagger.Binds
    fun provideAppInfo(appInformation: AD2AppInformation): AppInformation

    companion object {

        @dagger.Provides
        fun provideProvider(): Navigator = AD2Navigator

        @dagger.Provides
        fun provideWorkManager(application: Application): WorkManager = WorkManager.getInstance(application)

        @ApplicationScope
        @dagger.Provides
        fun provideLogger(): Logger = Logger(listOf(YandexMetrics()))

        @ApplicationScope
        @dagger.Provides
        fun provideCalendar(): Calendar = Calendar.getInstance()

        @ElementsIntoSet
        @dagger.Provides
        fun provideInitializers(appInformation: AppInformation): Set<Initializer> {
            return setOf(
                YandexMetricsInitializer(YandexMetricsInit(appInformation)),
                SystemInfoInitializer(),
                CrashHandlerInitializer(),
                HistoricalProcessExitReasonsInitializer(),
                StrictModeInitializer(),
                DevicePerformanceInitializer(),
            )
        }
    }
}

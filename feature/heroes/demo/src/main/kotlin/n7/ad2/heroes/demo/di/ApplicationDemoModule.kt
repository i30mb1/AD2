package n7.ad2.heroes.demo.di

import android.app.Application
import androidx.work.WorkManager
import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.app.logger.Logger
import n7.ad2.common.application.BaseApplicationModule
import n7.ad2.feature.heroes.ui.api.HeroesDependencies
import n7.ad2.hero.page.api.HeroPageProvider
import n7.ad2.navigator.Navigator
import n7.ad2.navigator.api.HeroPageApi

@dagger.Module(
    includes = [
        BaseApplicationModule::class,
    ],
)
internal interface ApplicationDemoModule {

    @dagger.Binds
    fun provideStreamsDependencies(impl: ApplicationComponentDemo): HeroesDependencies

    companion object {

        // the only real destination in this demo is the hero page, everything else stays empty
        @dagger.Provides
        fun navigator(): Navigator = object : Navigator by Navigator.empty() {
            override val heroPageApi: HeroPageApi = HeroPageProvider()
        }

        @dagger.Provides
        fun provideLogger(): Logger = Logger()

        @dagger.Provides
        fun provideWorkManager(application: Application): WorkManager = WorkManager.getInstance(application)

        @dagger.Provides
        fun provideAppInformation(): AppInformation = object : AppInformation {
            override val isDebug: Boolean = true
            // data follows the phone language like the UI strings do, both locales are checked on a device
            override val appLocale: AppLocale = if (java.util.Locale.getDefault().language == "ru") AppLocale.Russian else AppLocale.English
            override val appVersion: String = "1"
            override val isNightMode: Boolean = false
        }
    }
}

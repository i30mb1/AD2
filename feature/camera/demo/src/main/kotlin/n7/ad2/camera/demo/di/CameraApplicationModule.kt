package n7.ad2.camera.demo.di

import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.app.logger.Logger
import n7.ad2.common.application.BaseApplicationModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.navigator.Navigator

@dagger.Module(
    includes = [
        BaseApplicationModule::class,
    ]
)
internal interface CameraApplicationModule {

    companion object {

        @dagger.Provides
        fun navigator(): Navigator = Navigator.empty()

        @dagger.Provides
        @ApplicationScope
        fun provideLogger(): Logger = Logger()

        @dagger.Provides
        fun provideAppInformation(): AppInformation = object : AppInformation {
            override val isDebug: Boolean = true
            override val appLocale: AppLocale = AppLocale.English
            override val appVersion: String = "1"
            override val isNightMode: Boolean = false
        }
    }
}

package n7.ad2.news.demo.internal.di

import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.app.logger.Logger
import n7.ad2.apppreference.Preference
import n7.ad2.common.application.BaseApplicationModule
import n7.ad2.navigator.Navigator

@dagger.Module(
    includes = [
        BaseApplicationModule::class,
    ],
)
internal interface ApplicationDemoModule {

    companion object {

        @dagger.Provides
        fun navigator(): Navigator = Navigator.empty()

        @dagger.Provides
        fun provideLogger(): Logger = Logger()

        @dagger.Provides
        fun provideAppInformation(): AppInformation = object : AppInformation {
            override val isDebug: Boolean = true
            override val appLocale: AppLocale = AppLocale.English
            override val appVersion: String = "1"
            override val isNightMode: Boolean = false
        }

        // A no-op Preference is enough for the standalone demo; isNeedToUpdateNews() = true makes the
        // feed fetch fresh data on launch. Avoids pulling the AD2Preference + GetCurrentDayUseCase chain.
        @dagger.Provides
        fun providePreference(): Preference = object : Preference {
            override suspend fun isNeedToUpdateNews(): Boolean = true
            override suspend fun saveUpdateNewsDate() = Unit
            override suspend fun isNeedToUpdateSettings(): Boolean = true
            override suspend fun saveSettings(data: String) = Unit
            override suspend fun getSettings(): String = ""
            override suspend fun saveDate(date: Int) = Unit
            override suspend fun getDate(): Int = 0
            override suspend fun setFingerCoordinateEnabled(isEnabled: Boolean) = Unit
            override suspend fun isFingerCoordinateEnabled(): Boolean = false
            override suspend fun isLogWidgetEnabled(): Boolean = false
        }
    }
}

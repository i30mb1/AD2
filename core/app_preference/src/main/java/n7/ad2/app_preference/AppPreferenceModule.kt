package n7.ad2.app_preference

import android.app.Application
import n7.ad2.app_preference.domain.usecase.GetCurrentDayUseCase
import javax.inject.Singleton

@dagger.Module
class AppPreferenceModule {

    @Singleton
    @dagger.Provides
    fun provideAppPreference(
        application: Application,
        getCurrentDayUseCase: GetCurrentDayUseCase,
    ): AppPreference = AppPreference(application, getCurrentDayUseCase)

}
package n7.ad2.app_preference

import android.app.Application
import n7.ad2.app_preference.domain.usecase.GetCurrentDayUseCase
import n7.ad2.dagger.ApplicationScope

@dagger.Module
class AppPreferenceModule {

    @ApplicationScope
    @dagger.Provides
    fun provideAppPreference(
        application: Application,
        getCurrentDayUseCase: GetCurrentDayUseCase,
    ): AppPreference = AppPreference(application, getCurrentDayUseCase)

}
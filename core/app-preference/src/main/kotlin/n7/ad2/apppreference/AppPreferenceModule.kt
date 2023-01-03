package n7.ad2.apppreference

import android.app.Application
import n7.ad2.apppreference.domain.usecase.GetCurrentDayUseCase
import n7.ad2.dagger.ApplicationScope

@dagger.Module
object AppPreferenceModule {

    @ApplicationScope
    @dagger.Provides
    fun provideAppPreference(
        application: Application,
        getCurrentDayUseCase: GetCurrentDayUseCase,
    ): Preference = AD2Preference(application, getCurrentDayUseCase)

}
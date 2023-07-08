package n7.ad2.common.application

import android.app.Application
import n7.ad2.AppSettings
import n7.ad2.Resources

@dagger.Module
class BaseApplicationModule {

    @dagger.Reusable
    @dagger.Provides
    fun provideAppResource(application: Application): Resources = resourcesFactory(application)

    @dagger.Reusable
    @dagger.Provides
    fun provideAppSettings(): AppSettings = appSettingsFactory()
}

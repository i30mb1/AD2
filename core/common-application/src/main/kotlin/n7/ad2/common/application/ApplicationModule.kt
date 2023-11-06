package n7.ad2.common.application

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.AppSettings
import n7.ad2.Resources
import n7.ad2.dagger.ApplicationScope

@dagger.Module
class BaseApplicationModule {

    @dagger.Reusable
    @dagger.Provides
    fun provideAppResource(application: Application): Resources = Resources(application)

    @dagger.Reusable
    @dagger.Provides
    fun provideAppSettings(): AppSettings = appSettingsFactory()

//    @ApplicationScope
//    @dagger.Provides
//    fun moshi(): Moshi = Moshi.Builder().build()
}

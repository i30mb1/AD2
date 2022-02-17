package n7.ad2

import android.app.Application
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import javax.inject.Singleton

@dagger.Module
abstract class UpdateManagerModule {

    @Singleton
    @dagger.Provides
    fun provideUpdateManager(application: Application) = AppUpdateManagerFactory.create(application)

}
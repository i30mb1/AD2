package n7.ad2.updatemanager

import android.app.Application
import com.google.android.play.core.appupdate.AppUpdateManagerFactory

@dagger.Module
object UpdateManagerModule {

    @dagger.Provides
    fun provideUpdateManager(application: Application) = AppUpdateManagerFactory.create(application)
}

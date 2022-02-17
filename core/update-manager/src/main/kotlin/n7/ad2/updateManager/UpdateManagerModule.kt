package n7.ad2.updateManager

import android.app.Application
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import n7.ad2.dagger.ApplicationScope

@dagger.Module
class UpdateManagerModule {

    @ApplicationScope
    @dagger.Provides
    fun provideUpdateManager(application: Application) = AppUpdateManagerFactory.create(application)

}
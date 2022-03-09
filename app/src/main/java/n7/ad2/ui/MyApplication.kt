package n7.ad2.ui

import android.app.Application
import android.provider.Settings
import n7.ad2.AppInformation
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies
import n7.ad2.di.ApplicationComponent
import n7.ad2.di.DaggerApplicationComponent
import n7.ad2.di.DaggerComponentProvider
import n7.ad2.init.CrashHandlerInitializer
import n7.ad2.init.HistoricalProcessExitReasonsInitializer
import n7.ad2.init.StrictModeInitializer
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.logger.AD2Logger
import javax.inject.Inject

const val ANDROID_ID = Settings.Secure.ANDROID_ID

// https://medium.com/bumble-tech/how-we-achieved-a-6x-reduction-of-anrs-part-2-fixing-anrs-24fedf9a973f
// “Code never lies, comments sometimes do” — Ron Jeffries
class MyApplication : Application(), DaggerComponentProvider, HasDependencies {
//
//    val saf = Settings.Secure.getString(
//        applicationContext.contentResolver,
//        Settings.Secure.ANDROID_ID
//    )

    @Inject override lateinit var dependenciesMap: DependenciesMap

    override val component: ApplicationComponent by lazyUnsafe { DaggerApplicationComponent.factory().create(this) }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
    }

    @Inject
    fun init(logger: AD2Logger, appInformation: AppInformation) {
        CrashHandlerInitializer().init(this, logger, appInformation)
        HistoricalProcessExitReasonsInitializer().init(this, logger, appInformation)
        StrictModeInitializer().init(this, logger, appInformation)
    }

}
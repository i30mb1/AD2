package n7.ad2.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.provider.Settings
import n7.ad2.BuildConfig
import n7.ad2.di.ApplicationComponent
import n7.ad2.di.DaggerApplicationComponent
import n7.ad2.di.DaggerComponentProvider

const val ANDROID_ID = Settings.Secure.ANDROID_ID

// https://medium.com/bumble-tech/how-we-achieved-a-6x-reduction-of-anrs-part-2-fixing-anrs-24fedf9a973f
// “Code never lies, comments sometimes do” — Ron Jeffries
class MyApplication : Application(), DaggerComponentProvider {
//
//    val saf = Settings.Secure.getString(
//        applicationContext.contentResolver,
//        Settings.Secure.ANDROID_ID
//    )

    override val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        enableStrictMode()
        super.onCreate()
    }

    private fun enableStrictMode() {
        if (BuildConfig.DEBUG) {
            Handler(Looper.getMainLooper()).postAtFrontOfQueue {
                StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder()
                        .detectDiskWrites()
                        .detectNetwork()
                        .detectCustomSlowCalls()
                        .detectResourceMismatches()
                        .penaltyLog()
                        .build()
                )
                StrictMode.setVmPolicy(
                    StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build()
                )
            }
        }
    }
}

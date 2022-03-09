package n7.ad2.ui

import android.os.Bundle
import android.view.MotionEvent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.strictmode.FragmentStrictMode
import n7.ad2.BuildConfig
import n7.ad2.android.Navigator
import n7.ad2.android.SplashScreen
import n7.ad2.android.TouchEvent
import n7.ad2.databinding.ActivityMainBinding
import n7.ad2.di.injector
import n7.ad2.provider.Provider
import n7.ad2.updateManager.IsNewAppVersionAvailable
import javax.inject.Inject

class MainActivity : FragmentActivity(), TouchEvent, SplashScreen, Navigator {

    @Inject lateinit var provider: Provider

    @Inject lateinit var isNewAppVersionAvailable: IsNewAppVersionAvailable

    override var dispatchTouchEvent: ((event: MotionEvent) -> Unit)? = null
    override var shouldKeepOnScreen = true
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.component.inject(this)
        installSplashScreen().setKeepVisibleCondition(::shouldKeepOnScreen)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupInsets()
        setupFragmentStrictPolicy()
        if (savedInstanceState == null) setMainFragment(provider.drawerApi.getDrawerFragment())
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        dispatchTouchEvent?.invoke(event)
        return super.dispatchTouchEvent(event)
    }

    override fun setMainFragment(fragment: Fragment, body: FragmentTransaction.() -> Unit) {
        supportFragmentManager.commit(true) {
            body()
            replace(binding.container.id, fragment)
        }
    }

    private fun setupFragmentStrictPolicy() {
        if (BuildConfig.DEBUG) {
            supportFragmentManager.strictModePolicy = FragmentStrictMode.Policy.Builder()
                .penaltyDeath()
                .detectFragmentReuse()
                .detectFragmentTagUsage()
                .detectRetainInstanceUsage()
                .detectSetUserVisibleHint()
                .detectTargetFragmentUsage()
                .detectWrongFragmentContainer()
                .build()
        }
    }

    private fun setupInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

}
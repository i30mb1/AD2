package n7.ad2.ui

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commitNow
import androidx.fragment.app.strictmode.FragmentStrictMode
import javax.inject.Inject
import n7.ad2.AppInformation
import n7.ad2.android.MainFragmentNavigator
import n7.ad2.android.SplashScreen
import n7.ad2.android.TouchEvent
import n7.ad2.app.logger.Logger
import n7.ad2.databinding.ActivityMainBinding
import n7.ad2.di.injector
import n7.ad2.navigator.Navigator
import n7.ad2.updatemanager.IsNewAppVersionAvailable

class MainActivity : FragmentActivity(), TouchEvent, SplashScreen, MainFragmentNavigator {

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var logger: Logger
    @Inject lateinit var isNewAppVersionAvailable: IsNewAppVersionAvailable
    @Inject lateinit var appInformation: AppInformation

    override var dispatchTouchEvent: ((event: MotionEvent) -> Unit)? = null
    override var shouldKeepOnScreen = true
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.component.inject(this)
        checkAppLink()
        installSplashScreen().setKeepOnScreenCondition(::shouldKeepOnScreen)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupInsets()
        setupFragmentStrictPolicy()
        if (savedInstanceState == null) setMainFragment(navigator.drawerApi.getDrawerFragment())
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        dispatchTouchEvent?.invoke(event)
        return super.dispatchTouchEvent(event)
    }

    override fun setMainFragment(fragment: Fragment, body: FragmentTransaction.() -> Unit) {
        supportFragmentManager.commitNow {
            body()
            replace(binding.container.id, fragment)
        }
    }

    private fun checkAppLink() {
        val action = intent?.action
        val data = intent?.data
        logger.log("action=$action")
        logger.log("data=$data")
    }

    private fun setupFragmentStrictPolicy() {
        if (appInformation.isDebug) {
            supportFragmentManager.strictModePolicy =
                FragmentStrictMode.Policy.Builder().penaltyDeath().detectFragmentReuse().detectFragmentTagUsage().detectRetainInstanceUsage().detectSetUserVisibleHint()
                    .detectTargetFragmentUsage().detectWrongFragmentContainer().build()
        }
    }

    private fun setupInsets() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.tvFps) { view, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBarsInsets.top)
            insets
        }
    }

}

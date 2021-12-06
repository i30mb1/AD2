package n7.ad2.ui

import android.os.Bundle
import android.view.MotionEvent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import n7.ad2.android.MainNavigator
import n7.ad2.android.SplashScreen
import n7.ad2.android.TouchEvent
import n7.ad2.databinding.ActivityMain2Binding
import n7.ad2.provider.AD2Provider

class MainActivity2 : FragmentActivity(), TouchEvent, SplashScreen, MainNavigator {

    override var dispatchTouchEvent: ((event: MotionEvent) -> Unit)? = null
    override var shouldKeepOnScreen = true
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepVisibleCondition(::shouldKeepOnScreen)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setupInsets()
        if (savedInstanceState == null) {
            supportFragmentManager.commit(true) {
                setMainFragment(AD2Provider.drawerApi.getDrawerFragment())
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        dispatchTouchEvent?.invoke(event)
        return super.dispatchTouchEvent(event)
    }

    override fun setMainFragment(fragment: Fragment) {
        supportFragmentManager.commit(true) {
            add(binding.container.id, fragment)
        }
    }

    private fun setupInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

}
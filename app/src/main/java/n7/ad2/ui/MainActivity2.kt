package n7.ad2.ui

import android.os.Bundle
import android.view.MotionEvent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import n7.ad2.android.SplashScreenApi
import n7.ad2.android.TouchEvent
import n7.ad2.android.extension.lazyUnsafe
import n7.ad2.databinding.ActivityMain2Binding
import n7.ad2.drawer.internal.DrawerFragment

class MainActivity2 : FragmentActivity(), TouchEvent, SplashScreenApi {

    override var dispatchTouchEvent: ((event: MotionEvent) -> Unit)? = null
    override var shouldKeepOnScreen = true
    private val mainNavigator: MainNavigator by lazyUnsafe { MainNavigator(supportFragmentManager, binding.container.id) }
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepVisibleCondition(::shouldKeepOnScreen)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setupInsets()
        if (savedInstanceState == null) {
            supportFragmentManager.commit(true) {
                val mainFragment = DrawerFragment.getInstance()
                add(binding.container.id, mainFragment)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        dispatchTouchEvent?.invoke(event)
        return super.dispatchTouchEvent(event)
    }

    fun openHeroPageFragment(heroName: String) {
        mainNavigator.openHeroPageFragment(heroName)
    }

    private fun setupInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

}
package n7.ad2.ui

import android.os.Bundle
import android.view.MotionEvent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.commit
import n7.ad2.databinding.ActivityMain2Binding
import n7.ad2.ui.main.MainFragment
import n7.ad2.ui.main.TouchEvent
import n7.ad2.utils.BaseActivity

class MainActivity2 : BaseActivity(), TouchEvent {

    override var dispatchTouchEvent: ((event: MotionEvent) -> Unit)? = null
    var shouldKeepOnScreen = true
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepVisibleCondition(::shouldKeepOnScreen)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setupInsets()
        if (savedInstanceState == null) {
            supportFragmentManager.commit(true) {
                val mainFragment = MainFragment.getInstance()
                add(binding.container.id, mainFragment)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        dispatchTouchEvent?.invoke(event)
        return super.dispatchTouchEvent(event)
    }

    private fun setupInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

}
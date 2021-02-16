package n7.ad2.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.transition.TransitionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.databinding.ActivitySplashBinding
import n7.ad2.di.injector
import n7.ad2.ui.MainActivity
import n7.ad2.ui.setting.SettingsFragment.Companion.THEME_DARK
import n7.ad2.ui.setting.SettingsFragment.Companion.THEME_WHITE
import n7.ad2.utils.viewModel

class SplashActivity : FragmentActivity() {

    companion object {
        const val DELAY_START_ACTIVITY = 1000L
        const val DELAY_FADE_ANIMATION = 600L
        const val FADE_DURATION_ANIMATION = 1000L
    }

    private var fadeIn = false
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModel { injector.splashViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.loadNews()
        lifecycleScope.launch(Dispatchers.IO) {
            if(!viewModel.shouldWeShowSplashScreen()) finishSplashActivityAndStartMainActivity()
        }
        setMySplashTheme()
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        PreferenceManager.setDefaultValues(this, R.xml.setting, false)

        if (savedInstanceState == null) { fadeIn = true }

        viewModel.textEmote.observe(this) { binding.tv.text = it }
    }

    private fun setMySplashTheme() {
        when (PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.setting_theme_key), null)) {
            THEME_WHITE -> setTheme(R.style.SplashTheme_White)
            THEME_DARK -> setTheme(R.style.SplashTheme_Dark)
            else -> setTheme(R.style.SplashTheme)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && fadeIn) {
            window.decorView.run {
                alpha = 0f
                animate().cancel()
                animate().setStartDelay(DELAY_FADE_ANIMATION).withEndAction { startAnimateText() }.alpha(1f).duration = FADE_DURATION_ANIMATION
            }
            fadeIn = false
        }
    }

    private fun startAnimateText() {
        lifecycleScope.launch {
            TransitionManager.beginDelayedTransition(binding.root as ViewGroup)
            binding.tv.visibility = View.VISIBLE
            delay(DELAY_START_ACTIVITY)
            finishSplashActivityAndStartMainActivity()
        }
    }

    private fun finishSplashActivityAndStartMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

}

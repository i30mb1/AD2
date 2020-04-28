package n7.ad2.ui.setting

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.databinding.ActivityLicenseBinding
import n7.ad2.utils.BaseActivity
import n7.ad2.utils.extension.showSnackbar

class LicensesActivity : BaseActivity() {

    lateinit var binding: ActivityLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_license)
        binding.iv.setOnLongClickListener {
            MediaPlayer.create(applicationContext, R.raw.does_this_unit_have_a_soul).start()
            binding.root.showSnackbar("?", Snackbar.LENGTH_INDEFINITE, "Yes, it does") {
                MediaPlayer.create(applicationContext, R.raw.yes_it_does).start()
                startBlinking()
            }
            true
        }
    }

    private fun startBlinking() {
        lifecycleScope.launch {
            ValueAnimator.ofObject(ArgbEvaluator(), 0, getColor(R.color.purple), getColor(R.color.red_500), 0).apply {
                duration = 2000
                addUpdateListener { animator -> binding.root.setBackgroundColor(animator.animatedValue as Int) }
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                start()
            }

        }
    }
}
package ad2.n7.settings.internal

import android.R
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import n7.ad2.android.extension.showSnackbar

class LicensesActivity : FragmentActivity() {

    lateinit var binding: ActivityLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityLicenseBinding.inflate(this, R.layout.activity_license)
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
        ValueAnimator.ofObject(ArgbEvaluator(), 0, getColor(R.color.holo_purple), getColor(R.color.holo_red_dark), 0).apply {
            duration = 2000
            addUpdateListener { animator -> binding.root.setBackgroundColor(animator.animatedValue as Int) }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }

}
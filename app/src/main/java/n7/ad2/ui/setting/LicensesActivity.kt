package n7.ad2.ui.setting

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import n7.ad2.R
import n7.ad2.databinding.ActivityLicenseBinding
import n7.ad2.utils.BaseActivity

class LicensesActivity : BaseActivity() {
    lateinit var binding: ActivityLicenseBinding
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_license)
        binding.iv.setOnLongClickListener {
            MediaPlayer.create(applicationContext, R.raw.does_this_unit_have_a_soul).start()
            val snackbar = Snackbar.make(binding.root, "?..", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Yes, it does") {
                MediaPlayer.create(applicationContext, R.raw.yes_it_does).start()
                startBlinking()
                snackbar.dismiss()
            }
            snackbar.show()
            true
        }
    }

    private fun startBlinking() {
        handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                val colorAnimation = ValueAnimator.ofObject(
                        ArgbEvaluator(),
                        0,
                        getColor(R.color.purple),
                        getColor(R.color.red_500),
                        0
                )
                colorAnimation.duration = 2000
                colorAnimation.addUpdateListener { animator -> binding.root.setBackgroundColor(animator.animatedValue as Int) }
                colorAnimation.start()
                handler.postDelayed(this, 2000)
            }
        })
    }
}
package n7.ad2.ui.heroPage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import n7.ad2.R
import n7.ad2.databinding.ActivityHeroPageBinding
import n7.ad2.utils.BaseActivity
import n7.ad2.utils.Utils

class HeroPageActivity : BaseActivity() {

    lateinit var binding: ActivityHeroPageBinding
    val audioExoPlayer: AudioExoPlayer by lazy { AudioExoPlayer(application, lifecycle, ::showDialogError) }

    companion object {
        const val HERO_NAME = "HERO_NAME"
        const val TN_PHOTO = "TN_PHOTO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hero_page)

        val heroName = intent.getStringExtra(HERO_NAME)!!

        setToolbar(heroName)
        setViewPager2(heroName)
    }

    fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    sharedElement.viewTreeObserver.removeOnPreDrawListener(this)
                    supportStartPostponedEnterTransition()
                    return true
                }
            })
    }

    private fun setViewPager2(heroName: String) {
        val viewPager2Adapter = ViewPager2Adapter(this, heroName)
        binding.vp.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tl, binding.vp) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.hero_info)
                1 -> getString(R.string.hero_sound)
                else -> getString(R.string.hero_guide)
            }
        }.attach()

        binding.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.toolbar.pageSelected(position)
            }
        })
    }

    private fun setToolbar(heroName: String) {
        binding.toolbar.loadHero(heroName)
    }

    @RequiresPermission(Manifest.permission.WRITE_SETTINGS)
    private fun writeSetting() {

    }

    private fun requestPermission() {
        val registerPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) writeSetting()
        }
        when {
            checkSelfPermission(Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED -> writeSetting()
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_SETTINGS) -> Unit
            else -> registerPermission.launch(Manifest.permission.WRITE_SETTINGS)
        }

    }

    fun startAnimation(context: Context, img: Toolbar, name: String?, isLooped: Boolean?, mActionBarSize: Int) {
        val bitmapFromAssets = Utils.getBitmapFromAssets(context, name)
        if (bitmapFromAssets != null) {
            // cut bitmaps to array of bitmaps
            val bitmaps: Array<Bitmap?>
            val FRAME_W = 32
            val FRAME_H = 32
            val NB_FRAMES = bitmapFromAssets.width / FRAME_W
            val FRAME_DURATION = 70 // in ms !
            val COUNT_X = bitmapFromAssets.width / FRAME_W
            val COUNT_Y = 1
            bitmaps = arrayOfNulls(NB_FRAMES)
            var currentFrame = 0
            for (i in 0 until COUNT_Y) {
                for (j in 0 until COUNT_X) {
                    bitmaps[currentFrame] = Bitmap.createBitmap(bitmapFromAssets, FRAME_W * j, FRAME_H * i, FRAME_W, FRAME_H)
                    // apply scale factor
                    bitmaps[currentFrame] = Bitmap.createScaledBitmap(bitmaps[currentFrame]!!, mActionBarSize, mActionBarSize, true)
                    if (++currentFrame >= NB_FRAMES) {
                        break
                    }
                }
            }
            // create animation programmatically
            val animation = AnimationDrawable()
            animation.isOneShot = isLooped!! // repeat animation
            for (i in 0 until NB_FRAMES) {
                animation.addFrame(BitmapDrawable(context.resources, bitmaps[i]), FRAME_DURATION)
            }
            // load animation on image
            img.navigationIcon = animation
            // start animation on image
            img.post { animation.start() }
        }
    }
}
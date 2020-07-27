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
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import coil.api.load
import com.google.android.material.tabs.TabLayoutMediator
import n7.ad2.R
import n7.ad2.data.source.local.Repository
import n7.ad2.databinding.ActivityHeroPageBinding
import n7.ad2.di.injector
import n7.ad2.utils.BaseActivity
import n7.ad2.utils.Utils
import n7.ad2.utils.viewModelWithSavedStateHandle

class HeroPageActivity : BaseActivity() {

    private lateinit var binding: ActivityHeroPageBinding
    private val viewModelHeroPage: HeroPageViewModel by viewModelWithSavedStateHandle { injector.heroPageViewModelFactory }

    companion object {
        const val HERO_NAME = "HERO_NAME"
        const val TN_PHOTO = "TN_PHOTO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hero_page)

        if (savedInstanceState == null) viewModelHeroPage.loadHero(intent.getStringExtra(HERO_NAME)!!)

        setToolbar()
        setViewPager2()
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

    private fun setViewPager2() {
        val viewPager2Adapter = ViewPager2Adapter(this)
        binding.vp.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tl, binding.vp) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.hero_info)
                1 -> getString(R.string.hero_sound)
                else -> getString(R.string.hero_guide)
            }
        }.attach()
    }

    private fun setToolbar() {
        viewModelHeroPage.hero.observe(this) {
            binding.minimap.load("file:///android_asset/${it.assetsPath}/${Repository.ASSETS_FILE_MINIMAP}")
        }
    }

    @RequiresPermission(Manifest.permission.WRITE_SETTINGS)
    private fun writeSetting() {

    }

    private fun requestPermission() {
        val registerPermission = registerForActivityResult(RequestPermission()) {

        }
        registerPermission.launch(Manifest.permission.WRITE_SETTINGS)
        when {
            checkSelfPermission(Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED -> {
                writeSetting()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_SETTINGS) -> {

            }
            else -> {
                registerPermission.launch(Manifest.permission.WRITE_SETTINGS)
            }
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
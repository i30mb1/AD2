package n7.ad2.ui.heroPage

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import coil.api.load
import com.google.android.material.tabs.TabLayoutMediator
import n7.ad2.R
import n7.ad2.data.source.local.Repository
import n7.ad2.databinding.ActivityHeroPageBinding
import n7.ad2.utils.BaseActivity

class HeroPageActivity : BaseActivity() {

    private lateinit var binding: ActivityHeroPageBinding
    private lateinit var heroName: String

    companion object {
        const val HERO_NAME = "HERO_NAME"
        const val TN_PHOTO = "TN_PHOTO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hero_page)

        heroName = intent.getStringExtra(HERO_NAME)!!

        setToolbar()
        setViewPager2()
//        supportPostponeEnterTransition()
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
        setSupportActionBar(binding.toolbar)
        binding.minimap.load("file:///android_asset/heroes/$heroName/${Repository.ASSETS_FILE_MINIMAP}")
//        try {
//            binding.toolbarActivityHeroFull.title = heroName
//
//            val styledAttributes = theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
//            val mActionBarSize = styledAttributes.getDimension(0, 40f).toInt() / 2
//            try {
//                var icon = Utils.getBitmapFromAssets(this@HeroFullActivity, String.format("heroes/%s/mini.webp", heroCode))
//                icon = Bitmap.createScaledBitmap(icon!!, mActionBarSize, mActionBarSize, false)
//                val iconDrawable: Drawable = BitmapDrawable(resources, icon)
//                binding.toolbarActivityHeroFull.navigationIcon = iconDrawable
//            } catch (e: NullPointerException) {
//                e.printStackTrace()
//            }
//
//            binding.toolbarActivityHeroFull.setNavigationOnClickListener { Utils.startAnimation(this@HeroFullActivity, binding.toolbarActivityHeroFull, "heroes/$heroCode/emoticon.webp", false, mActionBarSize) }
//
//        } catch (e: NullPointerException) {
//            e.printStackTrace()
//        }
    }
}
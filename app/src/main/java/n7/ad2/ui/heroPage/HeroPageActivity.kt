package n7.ad2.ui.heroPage

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import n7.ad2.R
import n7.ad2.databinding.ActivityHeroPageBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroInfo.HeroInfoViewModel
import n7.ad2.utils.BaseActivity
import n7.ad2.utils.viewModelWithSavedStateHandle

class HeroPageActivity : BaseActivity() {

    private lateinit var binding: ActivityHeroPageBinding

    companion object {
        const val HERO_NAME = "HERO_NAME"
        const val TN_PHOTO = "TN_PHOTO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hero_page)

        setToolbar()
        setViewPager2()
//        supportPostponeEnterTransition()
        setObservers()
    }

    private fun setObservers() {
//        viewModel!!.showSnackBar.observe(this, Observer { integer ->
//            if (integer == HeroInfoViewModel.FILE_EXIST) {
//                Snackbar.make(binding.root, R.string.hero_responses_sound_already_downloaded, Snackbar.LENGTH_LONG).setAction(R.string.open_file) { view ->
//                    val selectedUri = Uri.parse(view.context.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).toString() + File.separator)
//                    val intentOpenFile = Intent(Intent.ACTION_VIEW)
//                    intentOpenFile.setDataAndType(selectedUri, "application/*")
//                    if (intentOpenFile.resolveActivityInfo(view.context.packageManager, 0) != null) {
//                        view.context.startActivity(Intent.createChooser(intentOpenFile, view.context.getString(R.string.hero_responses_open_folder_with)))
//                    } else {
//                        // if you reach this place, it means there is no any file
//                        // explorer app installed on your device
//                    }
//                }.show()
//            } else {
//                SnackbarUtils.showSnackbar(binding.root, getString(integer))
//            }
//        })
//        viewModel!!.grandPermission.observe(this, Observer { ActivityCompat.requestPermissions(this@HeroFullActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUESTED_PERMISSION) })
//        viewModel!!.grandSetting.observe(this, Observer { redId ->
//            SnackbarUtils.showSnackbarWithAction(binding.root, getString(redId!!), getString(R.string.all_enable)) {
//                @SuppressLint("InlinedApi") val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
//                intent.data = Uri.parse("package:" + application.packageName)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                application.startActivity(intent)
//            }
//        })
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
//        try {
//            sendBroadcast(Intent(MainActivity.LOG_ON_RECEIVE).putExtra(MainActivity.LOG_ON_RECEIVE, "hero_" + heroCode + "_loaded"))
//            binding.toolbarActivityHeroFull.title = heroName
//            setSupportActionBar(binding.toolbarActivityHeroFull)
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
package n7.ad2.hero_page.internal.pager

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.RequiresPermission
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import n7.ad2.AppInformation
import n7.ad2.android.findDependencies
import n7.ad2.hero_page.R
import n7.ad2.hero_page.databinding.FragmentHeroPageBinding
import n7.ad2.hero_page.internal.di.DaggerHeroPageComponent
import n7.ad2.ktx.lazyUnsafe
import javax.inject.Inject

class HeroPageFragment : Fragment(R.layout.fragment_hero_page) {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun getInstance(heroName: String): HeroPageFragment = HeroPageFragment().apply {
            arguments = bundleOf(
                HERO_NAME to heroName
            )
        }
    }

    @Inject lateinit var appInformation: AppInformation

    private var _binding: FragmentHeroPageBinding? = null
    private val binding: FragmentHeroPageBinding get() = _binding!!

    //    val audioExoPlayer: AudioExoPlayer by lazyUnsafe { AudioExoPlayer(requireContext(), lifecycle, ::showDialogError) }
    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHeroPageBinding.bind(view)

        setToolbar()
        setViewPager2()
        setupInsets()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vp.adapter = null
        _binding = null
    }

    fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    sharedElement.viewTreeObserver.removeOnPreDrawListener(this)
//                    supportStartPostponedEnterTransition()
                    return true
                }
            })
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(top = statusBarsInsets.top, bottom = navigationBarsInsets.bottom)
            insets
        }
    }

    private fun setViewPager2() {
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

    private fun setToolbar() {
        binding.toolbar.loadHero(heroName, appInformation.appLocale)
    }

    @RequiresPermission(Manifest.permission.WRITE_SETTINGS)
    private fun writeSetting() {

    }

    private fun requestPermission() {
//        val registerPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//            if (it) writeSetting()
//        }
//        when {
//            checkSelfPermission(requireContext(), Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED -> writeSetting()
//            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_SETTINGS) -> Unit
//            else -> registerPermission.launch(Manifest.permission.WRITE_SETTINGS)
//        }
    }

}
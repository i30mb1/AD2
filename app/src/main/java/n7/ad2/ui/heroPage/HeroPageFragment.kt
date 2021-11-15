package n7.ad2.ui.heroPage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroPageBinding
import n7.ad2.utils.lazyUnsafe

class HeroPageFragment : Fragment(R.layout.fragment_hero_page) {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun getInstance(heroName: String): HeroPageFragment = HeroPageFragment().apply {
            arguments = bundleOf(
                HERO_NAME to heroName
            )
        }
    }

    private var _binding: FragmentHeroPageBinding? = null
    private val binding: FragmentHeroPageBinding get() = _binding!!

    val audioExoPlayer: AudioExoPlayer by lazy(LazyThreadSafetyMode.NONE) { AudioExoPlayer(requireContext(), lifecycle, ::showDialogError) }
    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHeroPageBinding.bind(view)

        setToolbar(heroName)
        setViewPager2(heroName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
            checkSelfPermission(requireContext(), Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED -> writeSetting()
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_SETTINGS) -> Unit
            else -> registerPermission.launch(Manifest.permission.WRITE_SETTINGS)
        }
    }

}
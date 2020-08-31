package n7.ad2.ui.heroGuide

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroGuideBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroPage.HeroPageViewModel
import n7.ad2.utils.viewModelWithSavedStateHandle

class HeroGuideFragment : Fragment(R.layout.fragment_hero_guide) {

    private lateinit var binding: FragmentHeroGuideBinding
    private val viewModel: HeroGuideViewModel by viewModelWithSavedStateHandle { injector.heroGuideViewModelFactory }
    private val heroPageViewModel by activityViewModels<HeroPageViewModel>()

    companion object {
        fun newInstance(): HeroGuideFragment {
            return HeroGuideFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroGuideBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.vieModel = viewModel
        }

        heroPageViewModel.hero.observe(viewLifecycleOwner, viewModel::loadHeroWithGuides)
        viewModel.guide.observe(viewLifecycleOwner) {
            it.guides[0]
        }
    }


}
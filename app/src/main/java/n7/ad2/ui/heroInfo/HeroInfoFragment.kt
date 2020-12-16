package n7.ad2.ui.heroInfo

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroInfoBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroPage.HeroPageActivity
import n7.ad2.utils.viewModelWithSavedStateHandle

class HeroInfoFragment : Fragment(R.layout.fragment_hero_info) {

    private lateinit var binding: FragmentHeroInfoBinding
    private val viewModel: HeroInfoViewModel by viewModelWithSavedStateHandle { injector.heroInfoViewModelFactory }
    private val infoPopupWindow: InfoPopupWindow by lazy { InfoPopupWindow(requireContext(), lifecycle) }

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): HeroInfoFragment = HeroInfoFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroInfoBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }

        val heroName = requireArguments().getString(HERO_NAME)!!
        viewModel.loadHero(heroName)
        setupSpellInfoRecyclerView()
    }

    private fun setupSpellInfoRecyclerView() {
        val audioExoPlayer = (requireActivity() as HeroPageActivity).audioExoPlayer
        val descriptionsListAdapter = DescriptionsListAdapter(audioExoPlayer, infoPopupWindow)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.rv.apply {
            adapter = descriptionsListAdapter
            layoutManager = linearLayoutManager
        }
    }

}
package n7.ad2.ui.heroInfo

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroInfoBinding
import n7.ad2.ui.heroPage.HeroPageFragment
import n7.ad2.utils.StickyHeaderDecorator
import n7.ad2.utils.lazyUnsafe
import n7.ad2.utils.viewModel2
import javax.inject.Inject

class HeroInfoFragment : Fragment(R.layout.fragment_hero_info) {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): HeroInfoFragment = HeroInfoFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @Inject lateinit var heroInfoFactory: HeroInfoViewModel.Factory

    private var _binding: FragmentHeroInfoBinding? = null
    private val binding: FragmentHeroInfoBinding get() = _binding!!
    private val viewModel: HeroInfoViewModel by viewModel2 {
        heroInfoFactory.create(heroName)
    }
    private val infoPopupWindow: InfoPopupWindow by lazyUnsafe { InfoPopupWindow(requireContext(), lifecycle) }
    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHeroInfoBinding.bind(view)

        setupSpellInfoRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSpellInfoRecyclerView() {
        val audioExoPlayer = (parentFragment as HeroPageFragment).audioExoPlayer
        val descriptionsListAdapter = DescriptionsListAdapter(audioExoPlayer, infoPopupWindow)
        val linearLayoutManager = LinearLayoutManager(requireContext())

        binding.rv.apply {
            adapter = descriptionsListAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(StickyHeaderDecorator(descriptionsListAdapter, this))
        }
    }

}
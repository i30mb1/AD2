package n7.ad2.ui.heroInfo

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
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
    private lateinit var infoPopupWindow: InfoPopupWindow
    private lateinit var spellsAdapter: SpellsListAdapter

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): HeroInfoFragment = HeroInfoFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroInfoBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
            it.fragment = this
            it.ivImage.transitionName = requireActivity().intent.getStringExtra(HeroPageActivity.TN_PHOTO)
        }
        infoPopupWindow = InfoPopupWindow(requireContext(), lifecycle)


        viewModel.loadHero(requireArguments().getString(HERO_NAME)!!)
        setupSpellRecyclerView()
        setupSpellInfoRecyclerView()
    }

    fun setDescription(voDescription: List<VODescription>) {
        spellsAdapter.deselectAll()
        viewModel.vOHero.value?.selectedDescriptionList = voDescription
    }

    private fun setupSpellInfoRecyclerView() {
        val descriptionsListAdapter = DescriptionsListAdapter((requireActivity() as HeroPageActivity).audioExoPlayer, infoPopupWindow)

        binding.rvSpellsInfo.apply {
            adapter = descriptionsListAdapter
        }
    }

    private fun setupSpellRecyclerView() {
        spellsAdapter = SpellsListAdapter(this)

        binding.rvSpells.apply {
            LinearSnapHelper().attachToRecyclerView(this)
            adapter = spellsAdapter
        }
    }

}
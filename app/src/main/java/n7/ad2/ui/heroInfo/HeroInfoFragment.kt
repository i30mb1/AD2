package n7.ad2.ui.heroInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroInfoBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOTitleWithIcon
import n7.ad2.ui.heroPage.HeroPageActivity
import n7.ad2.utils.viewModelWithSavedStateHandle

class HeroInfoFragment : Fragment(R.layout.fragment_hero_info) {

    private lateinit var binding: FragmentHeroInfoBinding
    private val viewModel: HeroInfoViewModel by viewModelWithSavedStateHandle { injector.heroInfoViewModelFactory }
    private lateinit var infoPopupWindow: InfoPopupWindow
    private lateinit var spellsAdapter: SpellsListAdapter
    private lateinit var audioExoPlayer: AudioExoPlayer

    companion object {
        fun newInstance(): HeroInfoFragment {
            return HeroInfoFragment()
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
        audioExoPlayer = AudioExoPlayer(requireActivity().application, lifecycle)
        infoPopupWindow = InfoPopupWindow(requireContext(), lifecycle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        setupSpellRecyclerView()
        setupSpellInfoRecyclerView()
    }

    fun playAudio(view: View, model: VOTitleWithIcon) {
       if(model.audioUrl != null) {
           audioExoPlayer.play(model)
       }
    }

    fun setDescription(voDescription: List<VODescription>) {
        spellsAdapter.deselectAll()
        viewModel.vOHero.value?.selectedDescriptionList = voDescription
    }

    private fun setupSpellInfoRecyclerView() {
        val descriptionsListAdapter = DescriptionsListAdapter(this)

        binding.rvSpellsInfo.apply {
            adapter = descriptionsListAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
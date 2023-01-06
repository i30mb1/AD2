package n7.ad2.heropage.internal.info

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.android.findDependencies
import n7.ad2.heropage.R
import n7.ad2.heropage.databinding.FragmentHeroInfoBinding
import n7.ad2.heropage.internal.di.DaggerHeroPageComponent
import n7.ad2.heropage.internal.info.adapter.HeroInfoAdapter
import n7.ad2.heropage.internal.info.adapter.HeroInfoItemDecorator
import n7.ad2.heropage.internal.info.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.heropage.internal.info.domain.vo.VOSpell
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.ui.InfoPopupWindow
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
    private val viewModel: HeroInfoViewModel by viewModel { heroInfoFactory.create(heroName) }
    private val heroInfoItemDecorator = HeroInfoItemDecorator()
    private val infoPopupWindow: InfoPopupWindow by lazyUnsafe { InfoPopupWindow(requireContext(), lifecycle) }
    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }
    private val onKeyClickListener = { key: String -> }
    private val onHeroInfoCLickListener = { heroInfo: GetVOHeroDescriptionUseCase.HeroInfo -> viewModel.load(heroInfo) }
    private val onSpellClickListener = { spell: VOSpell.Simple -> viewModel.load(GetVOHeroDescriptionUseCase.HeroInfo.Spell(spell.name)) }
    private val onTalentClickListener = { spell: VOSpell.Talent -> viewModel.load(GetVOHeroDescriptionUseCase.HeroInfo.Spell(spell.name)) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHeroInfoBinding.bind(view)

        setupSpellInfoRecyclerView()
        setupInsets()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rv.adapter = null
        _binding = null
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            heroInfoItemDecorator.navigationBarsInsets = navigationBarsInsets.bottom
            binding.rv.invalidateItemDecorations()
            insets
        }
    }

    private fun doOnPlayIconClicked(soundUrl: String) {

    }

    private fun setupSpellInfoRecyclerView() {
        val heroInfoAdapter = HeroInfoAdapter(
            layoutInflater,
            infoPopupWindow,
            ::doOnPlayIconClicked,
            onKeyClickListener,
            onHeroInfoCLickListener,
            onSpellClickListener,
            onTalentClickListener,
        )
        val linearLayoutManager = LinearLayoutManager(requireContext())

        binding.rv.apply {
            adapter = heroInfoAdapter
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
//            addItemDecoration(StickyHeaderDecorator(heroInfoAdapter, this))
            addItemDecoration(heroInfoItemDecorator)
        }

        viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    is HeroInfoViewModel.State.Data -> {
                        binding.rv.isVisible = true
                        binding.error.isVisible = false
                        heroInfoAdapter.submitList(state.list) { binding.rv.invalidateItemDecorations() }
                    }
                    is HeroInfoViewModel.State.Error -> {
                        binding.rv.isVisible = false
                        binding.error.isVisible = true
                        binding.error.setError(state.error.stackTraceToString())
                    }
                    HeroInfoViewModel.State.Loading -> {

                    }
                }

            }
            .launchIn(lifecycleScope)
    }

}
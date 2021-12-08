package n7.ad2.hero_page.internal.info

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.android.extension.lazyUnsafe
import n7.ad2.android.extension.viewModel
import n7.ad2.hero_page.R
import n7.ad2.hero_page.databinding.FragmentHeroInfoBinding
import n7.ad2.hero_page.internal.info.adapter.HeroInfoAdapter
import n7.ad2.hero_page.internal.info.adapter.HeroInfoItemDecorator
import n7.ad2.hero_page.internal.info.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.hero_page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero_page.internal.info.domain.vo.VOSpell
import n7.ad2.hero_page.internal.pager.HeroPageFragment
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
    private val viewModel: HeroInfoViewModel by viewModel {
        heroInfoFactory.create(heroName)
    }
    private val infoPopupWindow: InfoPopupWindow by lazyUnsafe { InfoPopupWindow(requireContext(), lifecycle) }
    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }
    private val onPlayClickListener = { model: VOHeroInfo.HeaderSound -> }
    private val onKeyClickListener = { key: String -> }
    private val onHeroInfoCLickListener = { heroInfo: GetVOHeroDescriptionUseCase.HeroInfo -> viewModel.load(heroInfo) }
    private val onSpellClickListener = { spell: VOSpell -> viewModel.load(GetVOHeroDescriptionUseCase.HeroInfo.Spell(spell.name)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        val heroInfoAdapter = HeroInfoAdapter(
            layoutInflater,
            infoPopupWindow,
            onPlayClickListener,
            onKeyClickListener,
            onHeroInfoCLickListener,
            onSpellClickListener
        )
        val linearLayoutManager = LinearLayoutManager(requireContext())

        binding.rv.apply {
            adapter = heroInfoAdapter
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
//            addItemDecoration(StickyHeaderDecorator(heroInfoAdapter, this))
            addItemDecoration(HeroInfoItemDecorator())
        }

        viewModel.list.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { list ->
                heroInfoAdapter.submitList(list) {
                    binding.rv.invalidateItemDecorations()
                }
            }
            .launchIn(lifecycleScope)
    }

}
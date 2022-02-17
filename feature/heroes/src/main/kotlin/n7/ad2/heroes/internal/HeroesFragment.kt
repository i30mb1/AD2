package n7.ad2.heroes.internal

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.findDependencies
import n7.ad2.android.getNavigator
import n7.ad2.heroes.R
import n7.ad2.heroes.databinding.FragmentHeroesBinding
import n7.ad2.heroes.internal.adapter.HeroesItemDecorator
import n7.ad2.heroes.internal.adapter.HeroesListAdapter
import n7.ad2.heroes.internal.di.DaggerHeroesComponent
import n7.ad2.heroes.internal.domain.vo.VOHero
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.provider.Provider
import javax.inject.Inject

internal class HeroesFragment : Fragment(R.layout.fragment_heroes) {

    companion object {
        fun getInstance(): HeroesFragment = HeroesFragment()
    }

    @Inject lateinit var provider: Provider
    @Inject lateinit var heroesViewModelFactory: HeroesViewModel.Factory

    private var _binding: FragmentHeroesBinding? = null
    private val binding: FragmentHeroesBinding get() = _binding!!
    private val heroAdapter: HeroesListAdapter by lazyUnsafe { HeroesListAdapter(layoutInflater, onHeroClick) }
    private val viewModel: HeroesViewModel by viewModel { heroesViewModelFactory.create() }
    private val heroesItemDecorator = HeroesItemDecorator()
    private val onHeroClick: (hero: VOHero.Body) -> Unit = { hero -> startHeroFragment(hero) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // implement search for last queries https://developer.android.com/guide/topics/search/adding-recent-query-suggestions
    }

    private fun startHeroFragment(model: VOHero.Body) {
        getNavigator.setMainFragment(provider.heroPageApi.getPagerFragment(model.name)) {
            addToBackStack(null)
        }
        if (!model.viewedByUser) viewModel.updateViewedByUserFieldForHero(model.name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHeroesBinding.bind(view)
        setupAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapter() {
        val spanSizeItem = 1
        val spanSizeItemHeader = 3

        val gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (heroAdapter.getItemViewType(position)) {
                R.layout.item_header -> spanSizeItemHeader
                else -> spanSizeItem
            }
        }
        binding.rv.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(15)
            recycledViewPool.setMaxRecycledViews(R.layout.item_hero_body, 30)
            layoutManager = gridLayoutManager
            adapter = heroAdapter
            addItemDecoration(heroesItemDecorator)
//            postponeEnterTransition()
//            doOnPreDraw { startPostponedEnterTransition() }
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
                val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                heroesItemDecorator.statusBarsInsets = statusBarsInsets.top
                heroesItemDecorator.navigationBarsInsets = navigationBarsInsets.bottom
                insets
            }
            (parentFragment as DrawerPercentListener).setDrawerPercentListener { percent ->
                heroesItemDecorator.percent = percent
                invalidateItemDecorations()
            }
        }

        viewModel.filteredHeroes.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach(heroAdapter::submitList)
            .launchIn(lifecycleScope)
    }

}
package n7.ad2.ui.heroes

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.R
import n7.ad2.android.DrawerPercentListener
import n7.ad2.databinding.FragmentHeroesBinding
import n7.ad2.di.injector
import n7.ad2.ui.MainActivity2
import n7.ad2.ui.heroes.adapter.HeroesItemDecorator
import n7.ad2.ui.heroes.adapter.HeroesListAdapter
import n7.ad2.ui.heroes.domain.vo.VOHeroBody
import n7.ad2.utils.lazyUnsafe
import n7.ad2.utils.viewModel

class HeroesFragment : Fragment(R.layout.fragment_heroes) {

    companion object {
        fun getInstance(): HeroesFragment = HeroesFragment()
    }

    private var _binding: FragmentHeroesBinding? = null
    private val binding: FragmentHeroesBinding get() = _binding!!
    private val heroAdapter: HeroesListAdapter by lazyUnsafe { HeroesListAdapter(layoutInflater, onHeroClick) }
    private val viewModel: HeroesViewModel by viewModel { injector.heroesViewModel }
    private val heroesItemDecorator = HeroesItemDecorator()
    private val onHeroClick: (hero: VOHeroBody) -> Unit = { hero -> startHeroFragment(hero) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // implement search for last queries https://developer.android.com/guide/topics/search/adding-recent-query-suggestions
        inflater.inflate(R.menu.menu_search, menu)
        val searchHero = menu.findItem(R.id.action_search)
        val searchView = searchHero.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.filterHeroes(newText)
                return true
            }
        })
    }

    private fun startHeroFragment(model: VOHeroBody) {
        (activity as MainActivity2).openHeroPageFragment(model.name)
        if (!model.viewedByUser) viewModel.updateViewedByUserFieldForHero(model.name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHeroesBinding.bind(view)
        requireActivity().setTitle(R.string.heroes)
        setHasOptionsMenu(true) //вызов метода onCreateOptionsMenu в фрагменте
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

        viewModel.filteredHeroes.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach(heroAdapter::submitList)
            .launchIn(lifecycleScope)
    }

}
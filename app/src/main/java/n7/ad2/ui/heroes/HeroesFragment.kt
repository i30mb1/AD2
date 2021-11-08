package n7.ad2.ui.heroes

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroesBinding
import n7.ad2.databinding.ItemHeroBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroPage.HeroPageActivity
import n7.ad2.ui.heroes.domain.vo.VOHero
import n7.ad2.ui.main.DraggableDrawer
import n7.ad2.utils.ImageLoader
import n7.ad2.utils.viewModel
import javax.inject.Inject

class HeroesFragment : Fragment(R.layout.fragment_heroes) {

    companion object {
        fun getInstance(): HeroesFragment = HeroesFragment()
    }

    @Inject lateinit var imageLoader: ImageLoader
    private lateinit var binding: FragmentHeroesBinding
    private lateinit var heroAdapter: HeroesListAdapter

    private val viewModel: HeroesViewModel by viewModel { injector.heroesViewModel }
    private val heroesItemDecorator = HeroesItemDecorator()
    private val onHeroClick: (hero: VOHero) -> Unit = { }

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

    fun startHeroFragment(model: VOHero, binding: ItemHeroBinding) {
        Intent(binding.root.context, HeroPageActivity::class.java).apply {
            putExtra(HeroPageActivity.HERO_NAME, model.name)
            putExtra(HeroPageActivity.TN_PHOTO, binding.ivImage.transitionName)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), binding.ivImage, binding.ivImage.transitionName)
            startActivity(this)
        }

        if (!model.viewedByUser) viewModel.updateViewedByUserFieldForHero(model.name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroesBinding.bind(view)
        requireActivity().setTitle(R.string.heroes)
        setHasOptionsMenu(true) //вызов метода onCreateOptionsMenu в фрагменте
        setupAdapter()
    }


    private fun setupAdapter() {
        heroAdapter = HeroesListAdapter(layoutInflater, imageLoader, onHeroClick)
        binding.rv.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(15)
            recycledViewPool.setMaxRecycledViews(R.layout.item_hero, 30)
            layoutManager = GridLayoutManager(context, 3)
            adapter = heroAdapter
            addItemDecoration(heroesItemDecorator)
            postponeEnterTransition()
            doOnPreDraw { startPostponedEnterTransition() }
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
                val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                heroesItemDecorator.statusBarsInsets = statusBarsInsets.top
                heroesItemDecorator.navigationBarsInsets = navigationBarsInsets.bottom
                insets
            }
            (parentFragment as DraggableDrawer.Listener).setDrawerPercentListener { percent ->
                heroesItemDecorator.percent = percent
                binding.rv.invalidateItemDecorations()
            }
        }

        viewModel.filteredHeroes.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach(heroAdapter::submitList)
            .launchIn(lifecycleScope)
    }

}
package n7.ad2.ui.heroes

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.transition.Explode
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.Visibility
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.databinding.FragmentHeroesBinding
import n7.ad2.databinding.ItemHeroBinding
import n7.ad2.di.injector
import n7.ad2.ui.MainActivity
import n7.ad2.ui.heroPage.HeroPageActivity
import n7.ad2.utils.viewModel

class HeroesFragment : Fragment(R.layout.fragment_heroes) {

    private val viewModel: HeroesViewModel by viewModel { injector.heroesViewModel }
    private lateinit var binding: FragmentHeroesBinding
    private lateinit var heroAdapter: HeroesPagedListAdapter

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

    fun startHeroFragment(model: LocalHero, binding: ItemHeroBinding) {
        Intent(binding.root.context, HeroPageActivity::class.java).apply {
            putExtra(HeroPageActivity.HERO_NAME, model.name)
            putExtra(HeroPageActivity.TN_PHOTO, binding.iv.transitionName)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), binding.iv, binding.iv.transitionName)
            startActivity(this)
        }

        if (!model.viewedByUser) viewModel.updateViewedByUserFieldForHero(model.name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroesBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }
        requireActivity().apply {
            setTitle(R.string.heroes)
            sendBroadcast(Intent(MainActivity.LOG_ON_RECEIVE).putExtra(MainActivity.LOG_ON_RECEIVE, "heroes_activity_created"))
        }
        setHasOptionsMenu(true) //вызов метода onCreateOptionsMenu в фрагменте
        setupAdapter()
    }


    private fun setupAdapter() {
        heroAdapter = HeroesPagedListAdapter(this) // PagedListAdapter, заточенный под чтение данных из PagedList.
        binding.rv.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(15)
            recycledViewPool.setMaxRecycledViews(R.layout.item_hero, 30)
            layoutManager = GridLayoutManager(context, 3)
            this.adapter = heroAdapter
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener { startPostponedEnterTransition(); true }
        }
        viewModel.heroesPagedList.observe(viewLifecycleOwner, heroAdapter::submitList)
    }

    fun explode(rect: Rect) {
        var explode = Explode()
        explode.epicenterCallback = object : Transition.EpicenterCallback() {
            override fun onGetEpicenter(transition: Transition?): Rect {
                return rect
            }
        }
        explode.duration = resources.getInteger(R.integer.animation_medium).toLong()
        explode.mode = Visibility.MODE_OUT
        TransitionManager.beginDelayedTransition(binding.rv, explode)
        heroAdapter.submitList(null)
        heroAdapter.notifyDataSetChanged()
        lifecycleScope.launch {
            delay(resources.getInteger(R.integer.animation_medium).toLong() * 2)
            explode = Explode()
            explode.duration = resources.getInteger(R.integer.animation_medium).toLong()
            explode.mode = Visibility.MODE_IN
            TransitionManager.beginDelayedTransition(binding.rv, explode)
            viewModel.heroesPagedList.observe(viewLifecycleOwner, heroAdapter::submitList)
            heroAdapter.notifyDataSetChanged()
        }
    }
}
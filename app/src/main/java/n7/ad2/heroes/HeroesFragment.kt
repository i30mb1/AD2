package n7.ad2.heroes

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroesBinding
import n7.ad2.heroes.db.HeroModel
import n7.ad2.heroes.full.HeroFullActivity
import n7.ad2.main.MainViewModel
import n7.ad2.ui.MainActivity

class HeroesFragment : Fragment(R.layout.fragment_heroes) {

    private lateinit var viewModel: HeroesViewModel
    private lateinit var binding: FragmentHeroesBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // implement search for last queries https://developer.android.com/guide/topics/search/adding-recent-query-suggestions
        inflater.inflate(R.menu.menu_search, menu)
        val searchHero = menu.findItem(R.id.action_search)
        val searchView = searchHero.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val adapter = HeroesPagedListAdapter(this@HeroesFragment)
                binding.rv.adapter = adapter
                viewModel.getHeroesByFilter(newText).observe(viewLifecycleOwner, Observer { heroModels -> adapter.submitList(heroModels) })
                return true
            }
        })
    }

    fun startHeroFull(view: View, model: HeroModel) {
        val intent = Intent(view.context, HeroFullActivity::class.java)
        intent.putExtra(HeroFullActivity.HERO_NAME, model.name)
        intent.putExtra(HeroFullActivity.HERO_CODE_NAME, model.codeName)
        if (activity != null) {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), view, "iv")
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroesBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().apply {
            setTitle(R.string.heroes)
            sendBroadcast(Intent(MainActivity.LOG_ON_RECEIVE).putExtra(MainActivity.LOG_ON_RECEIVE, "heroes_activity_created"))
        }
        retainInstance = true //фрагмент не уничтожается а передаётся новому активити (пропускает методы onCreate&onDestroy)
        setHasOptionsMenu(true) //вызов метода onCreateOptionsMenu в фрагменте
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(this).get(HeroesViewModel::class.java)
        setupAdapter()
    }

    private fun setupAdapter() {
        val myAdapter = HeroesPagedListAdapter(this) // PagedListAdapter, заточенный под чтение данных из PagedList.
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 3)
            adapter = myAdapter
        }
        viewModel.getHeroesByFilter("").observe(viewLifecycleOwner, myAdapter::submitList)
    }
}
package n7.ad2.heroes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroesBinding
import n7.ad2.heroes.db.HeroModel
import n7.ad2.heroes.full.HeroFullActivity
import n7.ad2.main.MainViewModel
import n7.ad2.ui.MainActivity

class HeroesFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var viewModel: HeroesViewModel
    private lateinit var binding: FragmentHeroesBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val searchHero = menu.findItem(R.id.action_search)
        val searchView = searchHero.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    fun startHeroFull(view: View, model: HeroModel) {
        val intent = Intent(view.context, HeroFullActivity::class.java)
        intent.putExtra(HeroFullActivity.HERO_NAME, model.name)
        intent.putExtra(HeroFullActivity.HERO_CODE_NAME, model.codeName)
        if (activity != null) {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, view, "iv")
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_heroes, container, false)
        return binding.getRoot()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


//        getActivity().setTitle(getString(R.string.ad2_toolbar, getString(R.string.heroes)));
        activity!!.setTitle(R.string.heroes)
        activity!!.sendBroadcast(Intent(MainActivity.LOG_ON_RECEIVE).putExtra(MainActivity.LOG_ON_RECEIVE, "heroes_activity_created"))
        retainInstance = true //фрагмент не уничтожается а передаётся новому активити (пропускает методы onCreate&onDestroy)
        setHasOptionsMenu(true) //вызов метода onCreateOptionsMenu в фрагменте
        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(this).get(HeroesViewModel::class.java)
        initPagedListAdapter()
    }

    private fun initPagedListAdapter() {
        binding!!.rvFragmentHeroes.setHasFixedSize(true) // если recyclerView не будет изменяться в размерах тогда ставим true
        binding!!.rvFragmentHeroes.layoutManager = GridLayoutManager(context, 3)
        binding!!.rvFragmentHeroes.itemAnimator = DefaultItemAnimator()
        val adapter = HeroesPagedListAdapter(this) // PagedListAdapter, заточенный под чтение данных из PagedList.
        binding!!.rvFragmentHeroes.adapter = adapter
        viewModel!!.getHeroesByFilter("").observe(this, Observer { heroModels -> adapter.submitList(heroModels) })
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(s: String): Boolean {
        val adapter = HeroesPagedListAdapter(this)
        binding!!.rvFragmentHeroes.adapter = adapter
        viewModel!!.getHeroesByFilter(s).observe(this, Observer { heroModels -> adapter.submitList(heroModels) })
        return true
    }
}
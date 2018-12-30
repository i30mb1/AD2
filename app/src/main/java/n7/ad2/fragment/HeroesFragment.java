package n7.ad2.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.adapter.HeroesPagedListAdapter;
import n7.ad2.db.heroes.Heroes;
import n7.ad2.viewModels.HeroesViewModel;


public class HeroesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private View view;
    private HeroesViewModel heroesViewModel;
    private HeroesPagedListAdapter heroesPagedListAdapter;

    public HeroesFragment() {
        MySharedPreferences.LAST_FRAGMENT_SELECTED = 1;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchHero = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchHero.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_heroes, container, false);

        setRetainInstance(true);//фрагмент не уничтожается а передаётся новому активити (пропускает методы onCreate&onDestroy)
        setHasOptionsMenu(true);//вызов метода onCreateOptionsMenu в фрагменте
        heroesViewModel = ViewModelProviders.of(this).get(HeroesViewModel.class);

        initPagedListAdapter();
        return view;
    }

    private void initPagedListAdapter() {
        RecyclerView recyclerView = view.findViewById(R.id.rv_fragment_heroes);
        recyclerView.setHasFixedSize(true);//если recyclerView не будет изменяться в размерах тогда ставим true
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        heroesPagedListAdapter = new HeroesPagedListAdapter();//это RecyclerView.Adapter, заточенный под чтение данных из PagedList.
        recyclerView.setAdapter(heroesPagedListAdapter);
        heroesViewModel.getPagedListHeroesFilter("").observe(this, new Observer<PagedList<Heroes>>() {
            @Override
            public void onChanged(@Nullable PagedList<Heroes> heroes) {
                heroesPagedListAdapter.submitList(heroes);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        heroesViewModel.getPagedListHeroesFilter(s.trim()).observe(this, new Observer<PagedList<Heroes>>() {
            @Override
            public void onChanged(@Nullable PagedList<Heroes> heroes) {
                heroesPagedListAdapter.submitList(heroes);
            }
        });
        return false;
    }
}

package n7.ad2.heroes;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import n7.ad2.R;
import n7.ad2.databinding.FragmentHeroesBinding;
import n7.ad2.heroes.db.HeroModel;
import n7.ad2.main.MainViewModel;


public class HeroesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private HeroesViewModel viewModel;
    private FragmentHeroesBinding binding;
    private MainViewModel mainViewModel;

    public HeroesFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchHero = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchHero.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_heroes, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(R.string.heroes);
        setRetainInstance(true);//фрагмент не уничтожается а передаётся новому активити (пропускает методы onCreate&onDestroy)
        setHasOptionsMenu(true);//вызов метода onCreateOptionsMenu в фрагменте

        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel = ViewModelProviders.of(this).get(HeroesViewModel.class);

        initPagedListAdapter();
    }

    private void initPagedListAdapter() {
        binding.rvFragmentHeroes.setHasFixedSize(true);// если recyclerView не будет изменяться в размерах тогда ставим true
        binding.rvFragmentHeroes.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvFragmentHeroes.setItemAnimator(new DefaultItemAnimator());
        final HeroesPagedListAdapter adapter = new HeroesPagedListAdapter(); // PagedListAdapter, заточенный под чтение данных из PagedList.
        binding.rvFragmentHeroes.setAdapter(adapter);
        viewModel.getHeroesByFilter("").observe(this, new Observer<PagedList<HeroModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<HeroModel> heroModels) {
                adapter.submitList(heroModels);
            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        final HeroesPagedListAdapter adapter = new HeroesPagedListAdapter();
        binding.rvFragmentHeroes.setAdapter(adapter);
        viewModel.getHeroesByFilter(s).observe(this, new Observer<PagedList<HeroModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<HeroModel> heroModels) {
                adapter.submitList(heroModels);
            }
        });
        return true;
    }
}

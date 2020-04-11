package n7.ad2.heroes;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import n7.ad2.R;
import n7.ad2.databinding.FragmentHeroesBinding;
import n7.ad2.heroes.db.HeroModel;
import n7.ad2.heroes.full.HeroFullActivity;
import n7.ad2.main.MainViewModel;

import static n7.ad2.heroes.full.HeroFullActivity.HERO_CODE_NAME;
import static n7.ad2.heroes.full.HeroFullActivity.HERO_NAME;
import static n7.ad2.ui.MainActivity.LOG_ON_RECEIVE;


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

    public void startHeroFull(View view, HeroModel model) {
        Intent intent = new Intent(view.getContext(), HeroFullActivity.class);
        intent.putExtra(HERO_NAME, model.getName());
        intent.putExtra(HERO_CODE_NAME, model.getCodeName());
        if (getActivity() != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, "iv");
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_heroes, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


//        getActivity().setTitle(getString(R.string.ad2_toolbar, getString(R.string.heroes)));
        getActivity().setTitle(R.string.heroes);
        getActivity().sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "heroes_activity_created"));
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
        final HeroesPagedListAdapter adapter = new HeroesPagedListAdapter(this); // PagedListAdapter, заточенный под чтение данных из PagedList.
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
        final HeroesPagedListAdapter adapter = new HeroesPagedListAdapter(this);
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

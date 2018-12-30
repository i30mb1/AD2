package n7.ad2.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import n7.ad2.adapter.ItemsPagedListAdapter;
import n7.ad2.db.items.Items;
import n7.ad2.viewModels.ItemsViewModel;

public class ItemsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private View view;
    private ItemsViewModel itemsViewModel;
    private ItemsPagedListAdapter itemsPagedListAdapter;

    public ItemsFragment() {
        MySharedPreferences.LAST_FRAGMENT_SELECTED = 2;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchHero = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchHero.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_items, container, false);

        setRetainInstance(true);//фрагмент не уничтожается а передаётся новому активити
        setHasOptionsMenu(true);//вызов метода onCreateOptionsMenu в фрагменте
        itemsViewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);

        initPagedListAdapter();
        return view;
    }

    private void initPagedListAdapter() {
        RecyclerView recyclerView = view.findViewById(R.id.rv_fragment_items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 4));
        itemsPagedListAdapter = new ItemsPagedListAdapter();
        recyclerView.setAdapter(itemsPagedListAdapter);
        itemsViewModel.getPagedListFilter("").observe(this, new Observer<PagedList<Items>>() {
            @Override
            public void onChanged(@Nullable PagedList<Items> items) {
                itemsPagedListAdapter.submitList(items);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        itemsViewModel.getPagedListFilter(s.trim()).observe(this, new Observer<PagedList<Items>>() {
            @Override
            public void onChanged(@Nullable PagedList<Items> items) {
                itemsPagedListAdapter.submitList(items);
            }
        });
        return false;
    }
}

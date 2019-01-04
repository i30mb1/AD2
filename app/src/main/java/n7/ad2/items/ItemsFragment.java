package n7.ad2.items;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import n7.ad2.R;
import n7.ad2.databinding.FragmentItemsBinding;
import n7.ad2.items.db.ItemModel;

public class ItemsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private ItemsViewModel viewModel;
    private ItemsPagedListAdapter adapter;
    private FragmentItemsBinding binding;

    public ItemsFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_items, container, false);

        viewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);
        getActivity().setTitle(R.string.items);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        setupRecyclerView();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter = new ItemsPagedListAdapter();
        binding.rv.setAdapter(adapter);
        viewModel.getItemsByFilter("").observe(this, new Observer<PagedList<ItemModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<ItemModel> items) {
                adapter.submitList(items);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String chars) {
        viewModel.getItemsByFilter(chars.trim()).observe(this, new Observer<PagedList<ItemModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<ItemModel> items) {
                adapter.submitList(items);
            }
        });
        return true;
    }
}

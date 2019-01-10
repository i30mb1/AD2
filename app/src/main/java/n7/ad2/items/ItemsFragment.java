package n7.ad2.items;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
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
import n7.ad2.items.full.ItemFullActivity;
import n7.ad2.main.MainViewModel;

import static n7.ad2.items.full.ItemFullActivity.ITEM_CODE_NAME;
import static n7.ad2.items.full.ItemFullActivity.ITEM_NAME;
import static n7.ad2.main.MainActivity.LOG_ON_RECEIVE;

public class ItemsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private ItemsViewModel viewModel;
    private ItemsPagedListAdapter adapter;
    private FragmentItemsBinding binding;
    private MainViewModel mainViewModel;

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
        return binding.getRoot();
    }

    public void startItemFull(View view, ItemModel item) {
        Intent intent = new Intent(view.getContext(), ItemFullActivity.class);
        intent.putExtra(ITEM_CODE_NAME, item.getCodeName());
        intent.putExtra(ITEM_NAME, item.getName());
        if (getActivity() != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, "iv");
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        getActivity().setTitle(R.string.items);
        getActivity().sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "items_activity_created"));
        setRetainInstance(true);
        setHasOptionsMenu(true);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        binding.rvFragmentItems.setHasFixedSize(true);
        binding.rvFragmentItems.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter = new ItemsPagedListAdapter(this);
        binding.rvFragmentItems.setAdapter(adapter);
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

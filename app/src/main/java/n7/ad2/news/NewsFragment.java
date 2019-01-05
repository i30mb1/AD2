package n7.ad2.news;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.databinding.FragmentNewsBinding;
import n7.ad2.news.db.NewsModel;

public class NewsFragment extends Fragment {

    private FragmentNewsBinding binding;
    private NewsPagedListAdapter adapter;
    private NewsViewModel viewModel;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        getActivity().setTitle(R.string.news);
        setRetainInstance(true);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        binding.rvFragmentNews.setLayoutManager(gridLayoutManager);
        binding.rvFragmentNews.setHasFixedSize(true);

        boolean withImage = MySharedPreferences.getSharedPreferences(getContext()).getBoolean(getString(R.string.setting_news_key), true);

        adapter = new NewsPagedListAdapter(withImage);
        binding.rvFragmentNews.setAdapter(adapter);

        viewModel.getNews().observe(this, new Observer<PagedList<NewsModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<NewsModel> newsModels) {
                adapter.submitList(newsModels);
            }
        });
    }

}

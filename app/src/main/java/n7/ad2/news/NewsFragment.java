package n7.ad2.news;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import n7.ad2.R;
import n7.ad2.databinding.FragmentNewsBinding;
import n7.ad2.news.db.NewsModel;

import static n7.ad2.ui.MainActivity.LOG_ON_RECEIVE;

public class NewsFragment extends Fragment {

    private FragmentNewsBinding binding;
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
        getActivity().sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "news_activity_created"));
        setRetainInstance(true);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        binding.rvFragmentNews.setLayoutManager(gridLayoutManager);
//        binding.rvFragmentNews.setHasFixedSize(true);

        boolean withImage = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(R.string.setting_news_key), true);

        final NewsPagedListAdapter adapter = new NewsPagedListAdapter(withImage);
        binding.rvFragmentNews.setAdapter(adapter);

        viewModel.getNews().observe(this, new Observer<PagedList<NewsModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<NewsModel> newsModels) {
                adapter.submitList(newsModels);
            }
        });
    }

}

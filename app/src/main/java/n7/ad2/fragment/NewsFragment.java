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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import androidx.work.State;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.main.MainActivity;
import n7.ad2.adapter.NewsPagedListAdapter;
import n7.ad2.db.news.SteamNews;
import n7.ad2.viewModels.SteamNewsViewModel;
import n7.ad2.worker.SteamNewsWorker;

public class NewsFragment extends Fragment {

    ProgressBar progressBar;

    public NewsFragment() {
        MySharedPreferences.LAST_FRAGMENT_SELECTED = 3;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchHero = menu.findItem(R.id.action_search);
        searchHero.setVisible(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

//        progressBar = getActivity().findViewById(R.id.pb);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        initPagedListAdapter(view);
        return view;
    }

    private void initPagedListAdapter(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rv_fragment_news);
//        final ImageView iv_legion = getActivity().findViewById(R.id.iv_legion);
//        final TextView tv_legion = getActivity().findViewById(R.id.tv_legion);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        boolean withImage = MySharedPreferences.getSharedPreferences(getContext()).getBoolean(getString(R.string.setting_news_key), true);

        final NewsPagedListAdapter newsPagedListAdapter = new NewsPagedListAdapter(getActivity(),withImage);
        recyclerView.setAdapter(newsPagedListAdapter);

        SteamNewsViewModel steamNewsViewModel = ViewModelProviders.of(this).get(SteamNewsViewModel.class);
        steamNewsViewModel.getSteamNews().observe(this, new Observer<PagedList<SteamNews>>() {
            @Override
            public void onChanged(@Nullable PagedList<SteamNews> steamNews) {
                newsPagedListAdapter.submitList(steamNews);
//                if (newsPagedListAdapter.getItemCount() == 0) {
//                    iv_legion.setVisibility(View.VISIBLE);
//                    tv_legion.setVisibility(View.VISIBLE);
//                } else {
//                    iv_legion.setVisibility(View.INVISIBLE);
//                    tv_legion.setVisibility(View.INVISIBLE);
//                }
            }
        });

//        WorkManager.getInstance().getStatusesForUniqueWork(SteamNewsWorker.UNIQUE_WORK).observe(this, new Observer<List<WorkStatus>>() {
//            @Override
//            public void onChanged(@Nullable List<WorkStatus> workStatuses) {
//                if (workStatuses != null && workStatuses.size() == 1) {
//                    if (workStatuses.get(0).getState() == State.RUNNING) {
//                        ((MainActivity) getActivity()).log("work_news_running");
//                        progressBar.setVisibility(View.VISIBLE);
//                    } else if (workStatuses.get(0).getState() == State.ENQUEUED) {
//                        ((MainActivity) getActivity()).log("work_news_enqueued");
//                        progressBar.setVisibility(View.VISIBLE);
//                    } else if (workStatuses.get(0).getState() == State.SUCCEEDED) {
//                        progressBar.setVisibility(View.GONE);
//                        ((MainActivity) getActivity()).log("work_news_succeeded");
//                    } else if (workStatuses.get(0).getState() == State.FAILED) {
//                        progressBar.setVisibility(View.GONE);
//                        ((MainActivity) getActivity()).log("work_news_failed");
//                    }
//                }
//            }
//        });
    }
}

package n7.ad2.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.State;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.main.MainActivity;
import n7.ad2.adapter.GamesPagedListAdapter;
import n7.ad2.db.games.Games;
import n7.ad2.viewModels.GamesViewModel;
import n7.ad2.worker.GamesWorker;

import static n7.ad2.MySharedPreferences.PREMIUM;
import static n7.ad2.worker.GamesWorker.PAGE;
import static n7.ad2.worker.GamesWorker.UNIQUE_WORK;

public class TournamentsFragment extends Fragment {

    private View view;
    private ProgressBar progressBar;
    private ImageView iv_legion;
    private TextView tv_legion;
    private boolean oneTimeCheck = false;

    public TournamentsFragment() {
        MySharedPreferences.LAST_FRAGMENT_SELECTED = 4;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tournaments, container, false);

//        progressBar = getActivity().findViewById(R.id.pb);

        getActivity().setTitle(R.string.tournaments);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        startGamesWorker();
        initPagedListAdapter();
        initLegion();

        return view;
    }


    private void startGamesWorker() {
        Data data = new Data.Builder().putInt(PAGE, 0).putBoolean(GamesWorker.DELETE_TABLE, true).build();
        OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(GamesWorker.class).setInputData(data).build();
        WorkManager.getInstance().beginUniqueWork(UNIQUE_WORK, ExistingWorkPolicy.KEEP, worker).enqueue();
    }

    private void initLegion() {
//        iv_legion = getActivity().findViewById(R.id.iv_legion);
//        tv_legion = getActivity().findViewById(R.id.tv_legion);
        iv_legion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MediaPlayer.create(getContext(), R.raw.do_you_remember_the_question).start();
                return false;
            }
        });
    }

    private void initPagedListAdapter() {
        final RecyclerView recyclerView = view.findViewById(R.id.rv_fragment_games);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        final GamesPagedListAdapter gamesPagedListAdapter = new GamesPagedListAdapter(getViewLifecycleOwner(),MySharedPreferences.getSharedPreferences(getContext()).getBoolean(PREMIUM,false));
        recyclerView.setAdapter(gamesPagedListAdapter);

        GamesViewModel gamesViewModel = ViewModelProviders.of(this).get(GamesViewModel.class);
        gamesViewModel.getGames().observe(this, new Observer<PagedList<Games>>() {
            @Override
            public void onChanged(@Nullable PagedList<Games> games) {
                gamesPagedListAdapter.submitList(games);
                if (!oneTimeCheck&&games!=null) {
                    if (games.size()>0) {
                        oneTimeCheck = true;
                        iv_legion.setVisibility(View.INVISIBLE);
                        tv_legion.setVisibility(View.INVISIBLE);
                    } else {
                        iv_legion.setVisibility(View.VISIBLE);
                        tv_legion.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        WorkManager.getInstance().getStatusesForUniqueWork(GamesWorker.UNIQUE_WORK).observe(this, new Observer<List<WorkStatus>>() {
            @Override
            public void onChanged(@Nullable List<WorkStatus> workStatuses) {
                if (workStatuses != null && workStatuses.size() > 0) {
                    WorkManager.getInstance().pruneWork();
                    if (workStatuses.get(0).getState() == State.RUNNING) {
                        ((MainActivity) getActivity()).log("work_games_running");
                        progressBar.setVisibility(View.VISIBLE);
                    } else if (workStatuses.get(0).getState() == State.ENQUEUED) {
                        ((MainActivity) getActivity()).log("work_games_enqueued");
                        progressBar.setVisibility(View.VISIBLE);
                    } else if (workStatuses.get(0).getState() == State.SUCCEEDED) {
                        recyclerView.scrollToPosition(0);
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).log("work_games_succeeded");
                    } else if (workStatuses.get(0).getState() == State.FAILED) {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).log("work_games_failed");
                    }
                }
            }
        });
    }
}


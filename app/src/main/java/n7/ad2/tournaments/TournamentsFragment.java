package n7.ad2.tournaments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import n7.ad2.databinding.FragmentTournamentsBinding;
import n7.ad2.R;
import n7.ad2.tournaments.db.TournamentGame;

import static n7.ad2.setting.SettingActivity.SUBSCRIPTION;
import static n7.ad2.tournaments.TournamentsWorker.PAGE;
import static n7.ad2.tournaments.TournamentsWorker.TAG;

public class TournamentsFragment extends Fragment {

    private FragmentTournamentsBinding binding;
    private boolean subscription;
    private TournamentsViewModel viewModel;
    private TournamentsPagedListAdapter adapter;

    public TournamentsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tournaments, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(TournamentsViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        subscription = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(SUBSCRIPTION, false);
        getActivity().setTitle(R.string.tournaments);
        setRetainInstance(true);

        startGamesWorker();
        initPagedListAdapter();
    }

    private void startGamesWorker() {
        Data data = new Data.Builder().putInt(PAGE, 0).putBoolean(TournamentsWorker.DELETE_TABLE, true).build();
        OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(TournamentsWorker.class).setInputData(data).build();
        WorkManager.getInstance().beginUniqueWork(TAG, ExistingWorkPolicy.KEEP, worker).enqueue();
    }

    private void initPagedListAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        binding.rvFragmentTournaments.setLayoutManager(gridLayoutManager);
        binding.rvFragmentTournaments.setHasFixedSize(true);

        adapter = new TournamentsPagedListAdapter(getViewLifecycleOwner(),subscription);
        binding.rvFragmentTournaments.setAdapter(adapter);
        viewModel.getTournamentsGames().observe(this, new Observer<PagedList<TournamentGame>>() {
            @Override
            public void onChanged(@Nullable PagedList<TournamentGame> games) {
                adapter.submitList(games);
            }
        });
    }
}


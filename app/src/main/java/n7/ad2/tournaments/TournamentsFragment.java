package n7.ad2.tournaments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
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
import n7.ad2.R;
import n7.ad2.databinding.FragmentTournamentsBinding;
import n7.ad2.tournaments.db.TournamentGame;

import static n7.ad2.main.MainActivity.LOG_ON_RECEIVE;
import static n7.ad2.setting.SettingActivity.SUBSCRIPTION_PREF;
import static n7.ad2.tournaments.TournamentsWorker.PAGE;
import static n7.ad2.tournaments.TournamentsWorker.TAG;

public class TournamentsFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private FragmentTournamentsBinding binding;
    private ObservableBoolean subscription = new ObservableBoolean(false);
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

        subscription.set(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(SUBSCRIPTION_PREF, false));
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
        getActivity().setTitle(R.string.tournaments);
        getActivity().sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "tournaments_activity_created"));
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

        adapter = new TournamentsPagedListAdapter(getViewLifecycleOwner(), subscription);
        binding.rvFragmentTournaments.setAdapter(adapter);
        viewModel.getTournamentsGames().observe(this, new Observer<PagedList<TournamentGame>>() {
            @Override
            public void onChanged(@Nullable PagedList<TournamentGame> games) {
                adapter.submitList(games);
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SUBSCRIPTION_PREF)) {
            subscription.set(sharedPreferences.getBoolean(key, false));
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}


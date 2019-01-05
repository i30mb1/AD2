package n7.ad2.streams;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import n7.ad2.R;
import n7.ad2.databinding.FragmentStreamsBinding;
import n7.ad2.streams.retrofit.Streams;

import static n7.ad2.setting.SettingActivity.SUBSCRIPTION;

public class StreamsFragment extends Fragment {

    private FragmentStreamsBinding binding;
    private StreamsViewModel viewModel;
    private boolean subscription;

    public StreamsFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (subscription) {
            inflater.inflate(R.menu.menu_fragment_streams, menu);
        } else {
            inflater.inflate(R.menu.menu_fragment_streams_simple, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_streams, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(StreamsViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        subscription = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(SUBSCRIPTION, false);
        getActivity().setTitle(R.string.streams);
        setHasOptionsMenu(true);

        initRecyclerView();
    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        binding.rvFragmentStreams.setLayoutManager(gridLayoutManager);
        binding.rvFragmentStreams.setHasFixedSize(true);

        final StreamsPagedListAdapter adapter = new StreamsPagedListAdapter();
        binding.rvFragmentStreams.setAdapter(adapter);

        viewModel.getStreams().observe(this, new Observer<PagedList<Streams>>() {
            @Override
            public void onChanged(@Nullable PagedList<Streams> streams) {
                adapter.submitList(streams);
            }
        });
    }
}

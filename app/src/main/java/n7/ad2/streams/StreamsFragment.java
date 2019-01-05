package n7.ad2.streams;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.databinding.FragmentStreamsBinding;
import n7.ad2.main.MainViewModel;
import n7.ad2.viewModels.StreamsViewModel;

public class StreamsFragment extends Fragment {

    private boolean isPremium;
    private FragmentStreamsBinding binding;

    public StreamsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (isPremium) {
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

        MainViewModel mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        getActivity().setTitle(R.string.streams);

        initRecyclerView();
    }

    private void initRecyclerView() {
//        final TextView tv_legion = getActivity().findViewById(R.id.tv_legion);
//        final ImageView iv_legion = getActivity().findViewById(R.id.iv_legion);
//        final ProgressBar pb_fragment_streams = getActivity().findViewById(R.id.pb);
//        iv_legion.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                MediaPlayer.create(getContext(), R.raw.does_this_unit_have_a___).start();
//                tv_legion.setText("?????");
//                iv_legion.animate().alpha(0.0f).setDuration(8000L).start();
//                tv_legion.animate().alpha(0.0f).setDuration(8000L).start();
//                iv_legion.setLongClickable(false);
//                return true;
//            }
//        });

//        RecyclerView recyclerView = findViewById(R.id.tv_fragment_streams);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 1);
//        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.setHasFixedSize(true);
//
//        streamsPagedListAdapter = new StreamsPagedListAdapter();
//        recyclerView.setAdapter(streamsPagedListAdapter);

        StreamsViewModel streamsViewModel = ViewModelProviders.of(this).get(StreamsViewModel.class);
//        streamsViewModel.getStatusLoading().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable Boolean aBoolean) {
//                if (aBoolean != null && aBoolean) {
//                    ((MainActivity) getActivity()).log("streams_begin_loading");
//                    pb_fragment_streams.setVisibility(View.VISIBLE);
//                } else {
//                    ((MainActivity) getActivity()).log("streams_end_loading");
//                    pb_fragment_streams.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//        streamsViewModel.getStreams().observe(this, new Observer<PagedList<Streams>>() {
//            @Override
//            public void onChanged(@Nullable PagedList<Streams> streams) {
//                streamsPagedListAdapter.submitList(streams);
//                if (streamsPagedListAdapter.getItemCount() == 0) {
//                    iv_legion.setVisibility(View.VISIBLE);
//                    tv_legion.setVisibility(View.VISIBLE);
//                } else {
//                    iv_legion.setVisibility(View.INVISIBLE);
//                    tv_legion.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
    }
}

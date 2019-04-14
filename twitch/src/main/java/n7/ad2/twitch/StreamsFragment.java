package n7.ad2.twitch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;

import n7.ad2.twitch.databinding.DialogOpenStreamBinding;
import n7.ad2.twitch.databinding.FragmentStreamsBinding;
import n7.ad2.twitch.retrofit.Streams;

public class StreamsFragment extends Fragment {

    public static final String TWITCH_STREAMS_TYPED = "TWITCH_STREAMS_TYPED";
    public static final String TAG_MULTI_TWITCH = "TAG_MULTI_TWITCH";
    public static final String TAG_ONE_TWITCH = "TAG_ONE_TWITCH";
    private FragmentStreamsBinding binding;
    private StreamsViewModel viewModel;
    private boolean subscription;

    public StreamsFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_streams, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fragment_streams_open_by_name:
                createDialogOpenStream();
                break;
        }
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    private void createDialogOpenStream() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final DialogOpenStreamBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_open_stream, null, false);

        final AlertDialog dialog = builder.create();
        dialog.setView(binding.getRoot());
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();

        final LinkedList<String> list = new LinkedList<>();
        String streamTyped = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(TWITCH_STREAMS_TYPED, "[]");
        String arraysStreams[] = streamTyped.substring(1, streamTyped.length() - 1).split(", ");
        list.addAll(Arrays.asList(arraysStreams));
        for (final String i : list) {
            TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_list_stream_typed, null);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.tielDialogOpenStream.setText(i);
                }
            });
            tv.setText(i);
            binding.llDialogOpenStream.addView(tv);
        }

        binding.ibDialogOpenByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputTex = binding.tielDialogOpenStream.getText().toString().trim();
                if (!list.contains(inputTex)) list.addFirst(inputTex);
                while (list.size() > 4) {
                    list.pollLast();
                }
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(TWITCH_STREAMS_TYPED, Arrays.toString(list.toArray())).apply();
//                Intent intent = new Intent(getContext(), StreamsFullActivity.class);
//                intent.putExtra(CHANNEL_NAME, inputTex);
//                intent.putExtra(CHANNEL_TITLE, "");
//                startActivity(intent);
                dialog.dismiss();
            }
        });
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

        subscription = false;
        getActivity().setTitle("Streams");
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

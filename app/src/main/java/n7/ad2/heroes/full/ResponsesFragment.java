package n7.ad2.heroes.full;

import android.app.DownloadManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import n7.ad2.R;
import n7.ad2.databinding.FragmentHeroResponsesBinding;
import n7.ad2.utils.StickyHeaderDecorator;

public class ResponsesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private ResponsesPagedListAdapter responsesPagedListAdapter;
    private String currentLanguage;
    private FragmentHeroResponsesBinding binding;
    private HeroFulViewModel viewModel;
    //    private int initialKey;
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            viewModel.loadAvailableResponsesInMemory();
            responsesPagedListAdapter.notifyDataSetChanged();
            Snackbar.make(binding.getRoot(), R.string.responses_fragment_sound_downloaded, Snackbar.LENGTH_SHORT).setAction(R.string.open_file, new View.OnClickListener() {
                @SuppressWarnings("StatementWithEmptyBody")
                @Override
                public void onClick(View view) {
                    if (getContext() != null) {
                        Uri selectedUri = Uri.parse(getContext().getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator);
                        Intent intentOpenFile = new Intent(Intent.ACTION_VIEW);
                        intentOpenFile.setDataAndType(selectedUri, "application/*");
                        if (intentOpenFile.resolveActivityInfo(getContext().getPackageManager(), 0) != null) {
                            startActivity(Intent.createChooser(intentOpenFile, getString(R.string.all_open_folder)));
                        } else {
                            // if you reach this place, it means there is no any file
                            // explorer app installed on your device
                        }
                    }
                }
            }).show();
        }
    };
    private LinearLayoutManager linearLayoutManager;

    public static ResponsesFragment newInstance() {
        return new ResponsesFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_hero_responses, menu);

        MenuItem searchHero = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchHero.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    public String switchLanguage() {
        switch (currentLanguage) {
            case "ru":
                currentLanguage = "eng";
                break;
            default:
            case "eng":
                currentLanguage = "ru";
                break;
//            case "zh":
//                currentLanguage = "ru";
//                break;
        }
        return currentLanguage;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch_language:
                viewModel.loadResponses(switchLanguage());
                getResponses();
                item.setTitle(currentLanguage);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hero_responses, null, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(HeroFulViewModel.class);
//        if (savedInstanceState == null) {
//            initialKey = 0;
//        } else {
//            initialKey = savedInstanceState.getInt("initialKey");
//        }

        currentLanguage = getString(R.string.language_resource);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        setupPagedListAdapter();
        getResponses();
//        MyUtils.startSpriteAnim(getContext(), (ImageView) view.findViewById(R.id.iv_player2_heartRed2), "hero_responses_sprite.webp", true, 232, 232, 1000, 1);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("initialKey", linearLayoutManager.findFirstCompletelyVisibleItemPosition());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            getContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null) {
            getContext().unregisterReceiver(onComplete);
        }
    }

    private void setupPagedListAdapter() {
        binding.rvFragmentHeroResponses.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvFragmentHeroResponses.setLayoutManager(linearLayoutManager);
        responsesPagedListAdapter = new ResponsesPagedListAdapter(viewModel);
        binding.rvFragmentHeroResponses.addItemDecoration(new StickyHeaderDecorator(binding.rvFragmentHeroResponses, responsesPagedListAdapter));
        binding.rvFragmentHeroResponses.setAdapter(responsesPagedListAdapter);
    }

    private void getResponses() {
        viewModel.getResponsesPagedList("").observe(this, new Observer<PagedList<Response>>() {
            @Override
            public void onChanged(@Nullable PagedList<Response> responses) {
                responsesPagedListAdapter.submitList(responses);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        viewModel.getResponsesPagedList(newText).observe(this, new Observer<PagedList<Response>>() {
            @Override
            public void onChanged(@Nullable PagedList<Response> responses) {
                responsesPagedListAdapter.submitList(responses);
            }
        });
        return true;
    }

}

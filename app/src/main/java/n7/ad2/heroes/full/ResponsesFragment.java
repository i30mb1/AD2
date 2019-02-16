package n7.ad2.heroes.full;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import n7.ad2.R;
import n7.ad2.databinding.FragmentHeroResponsesBinding;
import n7.ad2.utils.SnackbarUtils;
import n7.ad2.utils.StickyHeaderDecorator;

public class ResponsesFragment extends Fragment implements SearchView.OnQueryTextListener {

    public static int REQUESTED_PERMISSION = 1;
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
        setObservers();
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
        responsesPagedListAdapter = new ResponsesPagedListAdapter(binding.getRoot(), viewModel);
        binding.rvFragmentHeroResponses.addItemDecoration(new StickyHeaderDecorator(binding.rvFragmentHeroResponses, responsesPagedListAdapter));
        binding.rvFragmentHeroResponses.setAdapter(responsesPagedListAdapter);
    }

    private void setObservers() {
        getResponses();
        viewModel.grandPermission.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (getActivity() != null) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTED_PERMISSION);
                }
            }
        });
        viewModel.grandSetting.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@StringRes Integer redId) {
                SnackbarUtils.showSnackbarWithAction(binding.getRoot(), getString(redId), getString(R.string.all_enable), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getContext() != null) {
                            @SuppressLint("InlinedApi") Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    private void getResponses() {
        viewModel.getResponsesPagedList("").observe(this, new Observer<PagedList<ResponseModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<ResponseModel> responseModels) {
                responsesPagedListAdapter.submitList(responseModels);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        viewModel.getResponsesPagedList(newText).observe(this, new Observer<PagedList<ResponseModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<ResponseModel> responseModels) {
                responsesPagedListAdapter.submitList(responseModels);
            }
        });
        return true;
    }

}

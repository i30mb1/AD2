package n7.ad2.heroes.full;

import android.app.DownloadManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import n7.ad2.utils.AppExecutors;
import n7.ad2.R;
import n7.ad2.utils.StickyHeaderDecorator;

public class HeroResponsesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private AppExecutors appExecutors;
    private View view;
    private String hero;
    private ResponsesPagedListAdapter responsesPagedListAdapter;
    private List<String> listSavedResponses = new ArrayList<>();
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            scanFolderForResponses();
            responsesPagedListAdapter.notifyDataSetChanged();
            Snackbar.make(view, R.string.hero_responses_fragment_sound_downloaded, Snackbar.LENGTH_LONG).setAction(R.string.open_file, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
            }).show();
        }
    };
    private String currentLanguage;
    private LiveData<PagedList<ResponseModel>> pagedList;
    private ResponsesRepository responsesRepository;

    public static HeroResponsesFragment newInstance(String hero, AppExecutors appExecutors) {
        HeroResponsesFragment fragment = new HeroResponsesFragment();
        fragment.hero = hero;
        fragment.appExecutors = appExecutors;
        return fragment;
    }

    private void scanFolderForResponses() {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (getContext() != null) {
                    File directory = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator + hero + File.separator);
                    File[] files = directory.listFiles();
                    if (files != null)
                        for (File file : files) {
                            if (!listSavedResponses.contains(file.getName()))
                                listSavedResponses.add(file.getName());
                        }
                }
            }
        });
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
                loadData(switchLanguage());
                item.setTitle(currentLanguage);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hero_responses, container, false);

        currentLanguage = getString(R.string.language_resource);
        if (getContext() != null)
            getContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        setHasOptionsMenu(true);
        setRetainInstance(true);

        scanFolderForResponses();
        initResponsesPagedAdapter();

//        MyUtils.startSpriteAnim(getContext(), (ImageView) view.findViewById(R.id.iv_player2_heartRed2), "hero_responses_sprite.webp", true, 232, 232, 1000, 1);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (getContext() != null)
            getContext().unregisterReceiver(onComplete);
        super.onDestroyView();
    }

    private void initResponsesPagedAdapter() {
        RecyclerView rv_fragment_hero_responses = view.findViewById(R.id.rv_fragment_hero_responses);
        rv_fragment_hero_responses.setHasFixedSize(true);
        rv_fragment_hero_responses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        responsesPagedListAdapter = new ResponsesPagedListAdapter(hero, appExecutors, getContext(), listSavedResponses, view);
        loadData(currentLanguage);
        rv_fragment_hero_responses.addItemDecoration(new StickyHeaderDecorator(rv_fragment_hero_responses, responsesPagedListAdapter));
        rv_fragment_hero_responses.setAdapter(responsesPagedListAdapter);
    }


    private void loadData(String locale) {
        if (getActivity() != null) {
            responsesRepository = new ResponsesRepository(getActivity().getApplication(), hero, locale);
            pagedList = responsesRepository.getPagedList("");
            pagedList.observe(this, new Observer<PagedList<ResponseModel>>() {
                @Override
                public void onChanged(@Nullable PagedList<ResponseModel> responseModels) {
                    responsesPagedListAdapter.submitList(responseModels);
                }
            });
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        pagedList = responsesRepository.getPagedList(newText);
        pagedList.observe(this, new Observer<PagedList<ResponseModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<ResponseModel> responseModels) {
                responsesPagedListAdapter.submitList(responseModels);
            }
        });
        return true;
    }

}

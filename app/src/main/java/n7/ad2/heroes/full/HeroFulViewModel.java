package n7.ad2.heroes.full;

import android.Manifest;
import android.app.Application;
import android.app.DownloadManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.ObservableBoolean;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import n7.ad2.R;
import n7.ad2.heroes.db.HeroModel;
import n7.ad2.heroes.db.HeroesDao;
import n7.ad2.heroes.db.HeroesRoomDatabase;
import n7.ad2.utils.SnackbarMessage;
import n7.ad2.utils.Utils;

import static n7.ad2.heroes.full.HeroFullActivity.HERO_CODE_NAME;
import static n7.ad2.setting.SettingActivity.SUBSCRIPTION_PREF;
import static n7.ad2.splash.SplashViewModel.CURRENT_DAY_IN_APP;

public class HeroFulViewModel extends AndroidViewModel {

    public final SnackbarMessage grandSetting = new SnackbarMessage();
    public final SnackbarMessage grandPermission = new SnackbarMessage();
    public final SnackbarMessage showSnackBar = new SnackbarMessage();
    public MutableLiveData<JSONObject> jsonObjectHeroFull = new MutableLiveData<>();
    public MutableLiveData<JSONArray> jsonArrayHeroAbilities = new MutableLiveData<>();
    public ObservableBoolean isGuideLoading = new ObservableBoolean(false);
    public MutableLiveData<HeroModel> hero = new MutableLiveData<>();
    public LinkedList<String> responsesInMemory = new LinkedList<>();
    public String heroCode;
    public String heroName;
    public HeroesDao heroesDao;
    private Application application;
    private Executor diskIO;
    private ResponsesStorage responsesStorage;

    public HeroFulViewModel(@NonNull Application application, String heroCode, String heroName) {
        super(application);
        this.application = application;
        this.heroCode = heroCode;
        this.heroName = heroName;
        diskIO = Executors.newSingleThreadExecutor();
        heroesDao = HeroesRoomDatabase.getDatabase(application, diskIO).heroesDao();

        loadHeroDescription(application.getString(R.string.language_resource));
        loadAvailableResponsesInMemory();
        loadResponses(application.getString(R.string.language_resource));
        laodHeroByCodeName(heroCode);
    }

    private void loadFreshGuideForHero(final HeroModel heroModel) {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                int currentDay = PreferenceManager.getDefaultSharedPreferences(application).getInt(CURRENT_DAY_IN_APP, 0);
                int guideLastDay = heroModel.getGuideLastDay();
                if (currentDay != guideLastDay) {
                    Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
                    Data data = new Data.Builder().putString(HERO_CODE_NAME, heroCode).build();
                    OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(GuideWorker.class)
                            .setInputData(data)
                            .setConstraints(constraints)
                            .build();
                    WorkManager.getInstance().enqueue(worker);
                    WorkManager.getInstance().getWorkInfoByIdLiveData(worker.getId()).observeForever(new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(@Nullable WorkInfo workInfo) {
                            if (workInfo != null) {
                                if (workInfo.getState().isFinished() || workInfo.getState().equals(WorkInfo.State.ENQUEUED)) {
                                    isGuideLoading.set(false);
                                } else {
                                    isGuideLoading.set(true);
                                }
                            }
                        }
                    });
                }
            }
        });

    }

    private void laodHeroByCodeName(final String heroCode) {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                HeroModel heroModel = heroesDao.getHeroByCodeNameObject(heroCode);
                loadFreshGuideForHero(heroModel);
            }
        });
    }


    public void loadResponses(final String language) {
        switch (language) {
//            case "zh":
//                responsesStorage = new ResponsesStorage(application, "heroes/" + heroFolder + "/zh_responses.json");
//                break;
            case "ru":
                responsesStorage = new ResponsesStorage(application, "heroes/" + heroCode + "/ru_responses.json", diskIO);
                break;
            default:
                responsesStorage = new ResponsesStorage(application, "heroes/" + heroCode + "/eng_responses.json", diskIO);
                break;
        }
        responsesStorage.load();
    }

    public void loadHeroDescription(final String language) {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(Utils.readJSONFromAsset(application, "heroes/" + heroCode + "/" + language + "_abilities.json"));
                    jsonObjectHeroFull.postValue(jsonObject);
                    JSONArray abilities = jsonObject.getJSONArray("abilities");
                    jsonArrayHeroAbilities.postValue(abilities);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadAvailableResponsesInMemory() {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                File directory = new File(application.getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator + heroCode + File.separator);
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (!responsesInMemory.contains(file.getName())) {
                            responsesInMemory.add(file.getName());
                        }
                    }
                }
            }
        });
    }

    public LiveData<PagedList<ResponseModel>> getResponsesPagedList(String search) {
        // DataSource это посредник между PagedList и Storage
        // PositionalResponsesDataSource dataSource = new PositionalResponsesDataSource(responsesStorage);

        // ResponsesSourceFactory фабрика, которую LivePagedListBuilder сможет использовать, чтобы самостоятельно создавать DataSource
        ResponsesSourceFactory sourceFactory = new ResponsesSourceFactory(responsesStorage, search);

        PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(20).build();

        // PagedList обёртка над List он содержит данные, умеет отдавать их а также подгружает новые
        // PagedList<ResponseModel> pagedList = new PagedList.Builder<>(dataSource,config).build();
        // обёртка над PagedList чтобы всё это происходило в бэкграунд потоке
        LiveData<PagedList<ResponseModel>> pagedListLiveData = new LivePagedListBuilder<>(sourceFactory, config)
//                .setInitialLoadKey(initialKey)
                .build();
        return pagedListLiveData;
    }

    public boolean userSubscription() {
        boolean subscription = PreferenceManager.getDefaultSharedPreferences(application).getBoolean(SUBSCRIPTION_PREF, false);
        if (isNetworkAvailable()) {
            return subscription;
        } else {
            return true;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    public boolean enableWriteSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(application)) {
                return true;
            } else {
                grandSetting.postValue(R.string.all_grand_permission);
                return false;
            }
        }
        return true;
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (application.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                grandPermission.call();
                return false;
            }
        }
        return true;
    }

    public void downloadResponse(ResponseModel model, AlertDialog dialog) {
        if (Utils.isNetworkAvailable(application)) {
            File file = new File(application.getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator + heroName + File.separator + model.getTitle().replace("?", "") + ".mp3");
            if (file.exists()) {
                showSnackBar.postValue(-7);
            } else {
                DownloadManager manager = (DownloadManager) application.getSystemService(Context.DOWNLOAD_SERVICE);
                if (manager != null) {
                    manager.enqueue(new DownloadManager.Request(Uri.parse(model.getHref()))
                            .setDescription(heroName)
                            .setTitle(model.getTitle())
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalFilesDir(application, Environment.DIRECTORY_RINGTONES, heroName + File.separator + model.getTitle().replace("?", "") + ".mp3")
                    );
                }
            }
        } else {
            showSnackBar.postValue(R.string.all_error_internet);
        }
        dialog.cancel();
    }

    public void setOnRingtone(ResponseModel model,AlertDialog dialog) {
        if (enableWriteSetting() && checkPermission()) {
            File file = new File(application.getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator + heroName + File.separator + model.getTitle().replace("?", "") + ".mp3");
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
                values.put(MediaStore.MediaColumns.TITLE, model.getTitle());
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                values.put(MediaStore.MediaColumns.SIZE, file.length());
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                values.put(MediaStore.Audio.Media.IS_ALARM, true);
                values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                ContentResolver contentResolver = application.getContentResolver();
                Uri generalaudiouri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + file.getAbsolutePath() + "'", null);
                Uri ringtoneuri = contentResolver.insert(generalaudiouri, values);
                RingtoneManager.setActualDefaultRingtoneUri(application, RingtoneManager.TYPE_NOTIFICATION, ringtoneuri);
                showSnackBar.postValue(R.string.hero_response_ringtone_set);
            } else {
                showSnackBar.postValue(R.string.hero_responses_fragment_download_first);
            }
        }
        dialog.cancel();
    }

}

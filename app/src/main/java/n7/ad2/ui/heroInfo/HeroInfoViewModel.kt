package n7.ad2.ui.heroInfo

import android.app.Application
import android.media.MediaPlayer
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.heroes.db.HeroModel
import n7.ad2.heroes.full.ResponseModel
import n7.ad2.heroes.full.ResponsesStorage
import n7.ad2.ui.heroInfo.domain.adapter.toVO
import n7.ad2.ui.heroInfo.domain.usecase.GetJsonHeroDescriptionUseCase
import n7.ad2.ui.heroInfo.domain.usecase.GetLocalHeroDescriptionFromJsonUseCase
import n7.ad2.ui.heroInfo.domain.vo.VOHeroDescription
import n7.ad2.utils.SnackbarMessage
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

//import com.google.android.exoplayer2.ExoPlaybackException;
//import com.google.android.exoplayer2.ExoPlayerFactory;
//import com.google.android.exoplayer2.Player;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.source.ProgressiveMediaSource;
//import com.google.android.exoplayer2.upstream.DataSource;
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
//import com.google.android.exoplayer2.util.Util;

interface ViewModelAssistedFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}

class HeroInfoViewModel @AssistedInject constructor(
        application: Application,
        appDatabase: AppDatabase,
        @Assisted handle: SavedStateHandle,
        private val getJsonHeroDescriptionUseCase: GetJsonHeroDescriptionUseCase,
        private val getLocalHeroDescriptionFromJsonUseCase: GetLocalHeroDescriptionFromJsonUseCase
) : AndroidViewModel(application) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HeroInfoViewModel>

    val grandSetting = SnackbarMessage()
    val grandPermission = SnackbarMessage()
    val showSnackBar = SnackbarMessage()
    var jsonObjectHeroFull = MutableLiveData<JSONObject>()
    var jsonArrayHeroAbilities = MutableLiveData<JSONArray>()
    var isGuideLoading = ObservableBoolean(false)
    var userSubscription = ObservableBoolean(false)
    var hero = MutableLiveData<HeroModel>()
    var responsesInMemory = LinkedList<String>()
    private var responsesStorage: ResponsesStorage? = null
    private var lastPlaying: ObservableBoolean? = null
    private var mediaPlayer: MediaPlayer? = null

    private val heroesDao = appDatabase.heroesDao
    val vOHero = MutableLiveData<VOHeroDescription>()

    init {
//        loadHeroDescription(application.getString(R.string.language_resource))
//        loadAvailableResponsesInMemory()
//        loadResponses(application.getString(R.string.language_resource))
//        laodHeroByCodeName(heroCode)
//        loadUserSubscription()
    }

    fun loadHero(name: String) {
        viewModelScope.launch {
            val hero = heroesDao.getHero(name)

            val json = getJsonHeroDescriptionUseCase(hero.assetsPath)
            val vOHeroDescription = getLocalHeroDescriptionFromJsonUseCase(json).toVO(hero)
            vOHero.value = vOHeroDescription
        }

    }

    private fun loadFreshGuideForHero(heroModel: HeroModel) {

//        int currentDay = PreferenceManager.getDefaultSharedPreferences(application).getInt(application.getString(R.string.setting_current_day), 0);
//        int guideLastDay = heroModel.getGuideLastDay();
//        if (currentDay != guideLastDay) {
//            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
//            Data data = new Data.Builder().putString(HERO_CODE_NAME, heroCode).build();
//            final OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(GuideWorker.class)
//                    .setInputData(data)
//                    .setConstraints(constraints)
//                    .build();
//            WorkManager.getInstance().enqueue(worker);
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    WorkManager.getInstance().getWorkInfoByIdLiveData(worker.getId()).observeForever(new Observer<WorkInfo>() {
//                        @Override
//                        public void onChanged(@Nullable WorkInfo workInfo) {
//                            if (workInfo != null) {
//                                if (workInfo.getState().isFinished() || workInfo.getState().equals(WorkInfo.State.ENQUEUED)) {
//                                    isGuideLoading.set(false);
//                                } else {
//                                    isGuideLoading.set(true);
//                                }
//                            }
//                        }
//                    });
//                }
//            });
//
//        }
    }

    private fun laodHeroByCodeName(heroCode: String) {
//        diskIO.execute {
//            val heroModel = heroesDao.getHeroByCodeNameObject(heroCode)
//            loadFreshGuideForHero(heroModel)
//        }
    }

    fun loadResponses(language: String?) {
//        responsesStorage = when (language) {
//            "ru" -> ResponsesStorage(application, "heroes/$heroCode/ru_responses.json", diskIO)
//            else -> ResponsesStorage(application, "heroes/$heroCode/eng_responses.json", diskIO)
//        }
//        responsesStorage!!.load()
    }

    fun loadHeroDescription(language: String) {
//        diskIO.execute {
//            try {
//                val jsonObject = JSONObject(Utils.readJSONFromAsset(application, "heroes/" + heroCode + "/" + language + "_abilities.json"))
//                jsonObjectHeroFull.postValue(jsonObject)
//                val abilities = jsonObject.getJSONArray("abilities")
//                jsonArrayHeroAbilities.postValue(abilities)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    fun loadAvailableResponsesInMemory() {
//        diskIO.execute {
//            val directory = File(application.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).toString() + File.separator + heroCode + File.separator)
//            val files = directory.listFiles()
//            if (files != null) {
//                for (file in files) {
//                    if (!responsesInMemory.contains(file.name)) {
//                        responsesInMemory.add(file.name)
//                    }
//                }
//            }
//        }
    }

//    fun getResponsesPagedList(search: String?): LiveData<PagedList<Response>> {
//        // DataSource это посредник между PagedList и Storage
//        // PositionalResponsesDataSource dataSource = new PositionalResponsesDataSource(responsesStorage);
//
//        // ResponsesSourceFactory фабрика, которую LivePagedListBuilder сможет использовать, чтобы самостоятельно создавать DataSource
//        val sourceFactory = ResponsesSourceFactory(responsesStorage, search)
//        val config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(20).build()
//
//        // PagedList обёртка над List он содержит данные, умеет отдавать их а также подгружает новые
//        // PagedList<ResponseModel> pagedList = new PagedList.Builder<>(dataSource,config).build();
//        // обёртка над PagedList чтобы всё это происходило в бэкграунд потоке
//        return LivePagedListBuilder(sourceFactory, config) //                .setInitialLoadKey(initialKey)
//                .build()
//    }

    fun loadUserSubscription() {
        userSubscription.set(true)
    }

    fun enableWriteSetting(): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (Settings.System.canWrite(application)) {
//                true
//            } else {
//                grandSetting.postValue(R.string.all_grand_permission)
//                false
//            }
//        } else true
        return true
    }

    fun checkPermission(): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (application.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                true
//            } else {
//                grandPermission.call()
//                false
//            }
//        } else true
        return true
    }

    fun startPlay(model: ResponseModel) {
//        try {
//            if (lastPlaying != null) {
//                lastPlaying!!.set(false)
//            }
//            model.playing.set(true)
//            lastPlaying = model.playing
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
//                startExoPlayer(model)
//                return
//            }
//            if (mediaPlayer != null) {
//                mediaPlayer!!.reset()
//                mediaPlayer!!.release()
//            }
//            mediaPlayer = MediaPlayer()
//            val file = File(application.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).toString() + File.separator + heroCode + File.separator + model.titleForFolder)
//            if (file.exists()) {
//                mediaPlayer!!.setDataSource(file.path)
//            } else {
//                mediaPlayer!!.setDataSource(model.href)
//            }
//            mediaPlayer!!.prepareAsync()
//            mediaPlayer!!.setOnPreparedListener { mediaPlayer!!.start() }
//            mediaPlayer!!.setOnCompletionListener { model.playing.set(false) }
//            mediaPlayer!!.setOnErrorListener { mp, what, extra ->
//                model.playing.set(false)
//                showErrorSnackbar()
//                true
//            }
//        } catch (e: IOException) {
//            model.playing.set(false)
//        }
    }

    private fun startExoPlayer(model: ResponseModel) {
//        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(application);
//        player.setPlayWhenReady(true);
//        // Produces DataSource instances through which media data is loaded.
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(application, Util.getUserAgent(application, "ad2"));
//// This is the MediaSource representing the media to be played.
//        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(model.getHref()));
//// Prepare the player with the source.
//        player.prepare(videoSource);
//        player.addListener(new Player.EventListener() {
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                if (playWhenReady && playbackState == Player.STATE_ENDED) {
//                    model.playing.set(false);
//                }
//            }
//
//            @Override
//            public void onPlayerError(ExoPlaybackException error) {
//                model.playing.set(false);
//                showErrorSnackbar();
//            }
//        });
    }

    private fun showErrorSnackbar() {
//        if (isNetworkAvailable) {
//            showSnackBar.postValue(R.string.all_something_went_wrong)
//        } else {
//            showSnackBar.postValue(R.string.all_error_internet)
//        }
    }

    // about download manager https://youtu.be/-4JqEROeI7U
    fun downloadResponse(model: ResponseModel, dialog: AlertDialog) {
//        if (isNetworkAvailable) {
//            val file = File(application.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).toString() + File.separator + heroCode + File.separator + model.titleForFolder)
//            if (file.exists()) {
//                showSnackBar.postValue(FILE_EXIST)
//            } else {
//                val manager = application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//                manager?.enqueue(DownloadManager.Request(Uri.parse(model.href))
//                        .setDescription(heroName)
//                        .setTitle(model.title)
//                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                        .setDestinationInExternalFilesDir(application, Environment.DIRECTORY_RINGTONES, heroCode + File.separator + model.titleForFolder)
//                )
//            }
//        } else {
//            showSnackBar.postValue(R.string.all_error_internet)
//        }
//        dialog.cancel()
    }

    fun setOnRingtone(model: ResponseModel, dialog: AlertDialog) {
//        if (enableWriteSetting() && checkPermission()) {
//            val file = File(application.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).toString() + File.separator + heroCode + File.separator + model.titleForFolder)
//            if (file.exists()) {
//                val values = ContentValues()
//                values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
//                values.put(MediaStore.MediaColumns.TITLE, model.title)
//                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
//                values.put(MediaStore.MediaColumns.SIZE, file.length())
//                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
//                values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
//                values.put(MediaStore.Audio.Media.IS_ALARM, true)
//                values.put(MediaStore.Audio.Media.IS_MUSIC, false)
//                val contentResolver = application.contentResolver
//                val generalaudiouri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
//                contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + file.absolutePath + "'", null)
//                val ringtoneuri = contentResolver.insert(generalaudiouri, values)
//                RingtoneManager.setActualDefaultRingtoneUri(application, RingtoneManager.TYPE_NOTIFICATION, ringtoneuri)
//                showSnackBar.postValue(R.string.hero_response_ringtone_set)
//            } else {
//                showSnackBar.postValue(R.string.hero_responses_fragment_download_first)
//            }
//        }
//        dialog.cancel()
    }

}
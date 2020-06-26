package n7.ad2.ui.heroInfo

import android.app.Application
import android.content.res.Resources
import android.media.MediaPlayer
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.heroes.db.HeroModel
import n7.ad2.heroes.full.ResponseModel
import n7.ad2.heroes.full.ResponsesSourceFactory
import n7.ad2.heroes.full.ResponsesStorage
import n7.ad2.ui.heroInfo.domain.interactor.GetHeroDescriptionInteractor
import n7.ad2.ui.heroResponse.domain.interactor.GetHeroResponsesInteractor
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
        private val getHeroDescriptionInteractor: GetHeroDescriptionInteractor
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

    fun loadHero(name: String, theme: Resources.Theme) {
        viewModelScope.launch {
            val localHero = heroesDao.getHero(name)
            vOHero.value = getHeroDescriptionInteractor(localHero, getApplication<Application>().getString(R.string.language_resource), theme)
        }
    }

    fun loadResponse(name: String) {
        viewModelScope.launch {
            val localHero = heroesDao.getHero(name)
//            voResponses.value = getHeroResponsesInteractor(localHero.assetsPath, getApplication<Application>().getString(R.string.language_resource))
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
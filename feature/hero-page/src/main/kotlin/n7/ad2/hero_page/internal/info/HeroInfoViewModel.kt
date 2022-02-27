package n7.ad2.hero_page.internal.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.ad2.AppInformation
import n7.ad2.hero_page.internal.info.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.hero_page.internal.info.domain.vo.VOHeroInfo

class HeroInfoViewModel @AssistedInject constructor(
    private val appInformation: AppInformation,
    private val getVOHeroDescriptionUseCase: GetVOHeroDescriptionUseCase,
    @Assisted private val heroName: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(heroName: String): HeroInfoViewModel
    }

    sealed class State {
        data class Data(val list: List<VOHeroInfo>) : State()
        data class Error(val error: Throwable) : State()
        object Loading : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        load(GetVOHeroDescriptionUseCase.HeroInfo.Main)
    }

    fun load(heroInfo: GetVOHeroDescriptionUseCase.HeroInfo) {
        viewModelScope.launch {
            getVOHeroDescriptionUseCase(heroName, appInformation.appLocale, heroInfo)
                .onEach { list -> _state.emit(State.Data(list)) }
                .catch { error -> _state.emit(State.Error(error)) }
                .launchIn(viewModelScope)
        }
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

//    fun setOnRingtone(model: ResponseModel, dialog: AlertDialog) {
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
//    }

}
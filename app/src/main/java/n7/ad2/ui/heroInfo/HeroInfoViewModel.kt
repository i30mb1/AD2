package n7.ad2.ui.heroInfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.data.source.local.Locale
import n7.ad2.ui.heroInfo.domain.interactor.GetHeroDescriptionInteractor
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroPage.domain.usecase.GetLocalHeroByNameUseCase
import javax.inject.Inject

interface ViewModelAssistedFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}

class HeroInfoViewModel @Inject constructor(
    application: Application,
    private val getHeroDescriptionInteractor: GetHeroDescriptionInteractor,
    private val getLocalHeroByNameUseCase: GetLocalHeroByNameUseCase,
) : AndroidViewModel(application) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HeroInfoViewModel>

    val vOHero = MutableLiveData<List<VODescription>>()

    @ExperimentalStdlibApi
    fun loadHero(heroName: String) {
        viewModelScope.launch {
            val locale = Locale.valueOf(getApplication<Application>().getString(R.string.locale))
            val localHero = getLocalHeroByNameUseCase(heroName)
            vOHero.value = getHeroDescriptionInteractor(localHero, locale)!!
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
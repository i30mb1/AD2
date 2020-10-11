package n7.ad2.ui.heroGuide

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import n7.ad2.ui.heroGuide.domain.usecase.ConvertLocalGuideJsonToVOHeroGuide
import n7.ad2.ui.heroGuide.domain.usecase.ConvertLocalHeroWithGuidesToLocalGuideJsonUseCase
import n7.ad2.ui.heroGuide.domain.usecase.GetLocalHeroWithGuidesUseCase
import n7.ad2.ui.heroGuide.domain.vo.VOHeroGuide
import n7.ad2.ui.heroInfo.ViewModelAssistedFactory

class HeroGuideViewModel @AssistedInject constructor(
    application: Application,
    @Assisted handle: SavedStateHandle,
    private val getLocalHeroWithGuidesUseCase: GetLocalHeroWithGuidesUseCase,
    private val convertLocalHeroWithGuidesToLocalGuideJsonUseCase: ConvertLocalHeroWithGuidesToLocalGuideJsonUseCase,
    private val convertLocalGuideJsonToVOHeroGuide: ConvertLocalGuideJsonToVOHeroGuide
) : AndroidViewModel(application) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HeroGuideViewModel>

    private val _guide: MutableLiveData<VOHeroGuide> = MutableLiveData()
    val guide: LiveData<VOHeroGuide> = _guide

    fun loadHeroWithGuides(heroName: String, context: Context) {
        viewModelScope.launch {
            getLocalHeroWithGuidesUseCase(heroName).collect {
                val localGuideJsonList = convertLocalHeroWithGuidesToLocalGuideJsonUseCase(it)
                if (localGuideJsonList.isNotEmpty()) _guide.postValue(convertLocalGuideJsonToVOHeroGuide(localGuideJsonList, context)[0])
            }

        }
    }

}
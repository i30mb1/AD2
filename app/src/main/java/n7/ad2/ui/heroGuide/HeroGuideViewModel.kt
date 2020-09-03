package n7.ad2.ui.heroGuide

import android.app.Application
import androidx.lifecycle.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import n7.ad2.data.source.local.model.LocalHero
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

    fun loadHeroWithGuides(localHero: LocalHero) {
        viewModelScope.launch {
            val heroWithGuidesUseCase = getLocalHeroWithGuidesUseCase(localHero.name)
            val localGuideJsonList = convertLocalHeroWithGuidesToLocalGuideJsonUseCase(heroWithGuidesUseCase)
            if (localGuideJsonList.isNotEmpty()) _guide.postValue(convertLocalGuideJsonToVOHeroGuide(localGuideJsonList, getApplication())[0])
        }
    }

}
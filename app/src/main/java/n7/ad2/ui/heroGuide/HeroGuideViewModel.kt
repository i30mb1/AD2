package n7.ad2.ui.heroGuide

import android.app.Application
import androidx.lifecycle.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.data.source.local.model.LocalHeroWithGuides
import n7.ad2.ui.heroGuide.domain.usecase.GetHeroWithGuidesUseCase
import n7.ad2.ui.heroInfo.ViewModelAssistedFactory

class HeroGuideViewModel @AssistedInject constructor(
    application: Application,
    @Assisted handle: SavedStateHandle,
    private val getHeroWithGuidesUseCase: GetHeroWithGuidesUseCase
) : AndroidViewModel(application) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HeroGuideViewModel>

    private val _guide: MutableLiveData<LocalHeroWithGuides> = MutableLiveData()
    val guide: LiveData<LocalHeroWithGuides> = _guide

    fun loadHeroWithGuides(localHero: LocalHero) {
        viewModelScope.launch {
            val heroWithGuidesUseCase = getHeroWithGuidesUseCase(localHero.name)
            _guide.value = heroWithGuidesUseCase
        }
    }

}
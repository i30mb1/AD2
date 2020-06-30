package n7.ad2.ui.heroPage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.ViewModelAssistedFactory
import n7.ad2.ui.heroPage.domain.usecase.GetLocalHeroByNameUseCase

class HeroPageViewModel @AssistedInject constructor(
        application: Application,
        @Assisted private val handle: SavedStateHandle,
        private val getLocalHeroByNameUseCase: GetLocalHeroByNameUseCase
) : AndroidViewModel(application) {

    companion object {
        private const val LOCAL_HERO_KEY = "LOCAL_HERO_KEY"
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HeroPageViewModel>

    private val _hero: MutableLiveData<String> = handle.getLiveData(LOCAL_HERO_KEY)
    val hero: LiveData<LocalHero> = _hero.switchMap {
        liveData {
            emit(getLocalHeroByNameUseCase(it))
        }
    }

    fun loadHero(name: String) = handle.set(LOCAL_HERO_KEY, name)

}
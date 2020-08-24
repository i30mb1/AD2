package n7.ad2.ui.heroPage

import android.app.Application
import androidx.lifecycle.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroGuide.HeroGuideWorker
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
            loadHeroGuide(it)
            emit(getLocalHeroByNameUseCase(it))
        }
    }

    fun refresh() {
        _hero.value = _hero.value
    }

    fun loadHero(name: String) = handle.set(LOCAL_HERO_KEY, name)

    private fun loadHeroGuide(heroName: String) {
        val data = workDataOf(HeroGuideWorker.HERO_NAME to heroName)
        val request = OneTimeWorkRequestBuilder<HeroGuideWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(getApplication()).enqueue(request)
    }
}
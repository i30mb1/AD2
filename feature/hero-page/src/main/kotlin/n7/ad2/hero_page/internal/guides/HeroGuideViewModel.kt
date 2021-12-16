package n7.ad2.hero_page.internal.guides

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import n7.ad2.hero_page.internal.guides.domain.interactor.GetVOHeroGuideItemsUseCase
import n7.ad2.hero_page.internal.guides.domain.vo.VOGuideItem

class HeroGuideViewModel @AssistedInject constructor(
    private val getVOHeroGuideItemsUseCase: GetVOHeroGuideItemsUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): HeroGuideViewModel
    }

    private val _error = Channel<Throwable>(Channel.BUFFERED)
    val error = _error.receiveAsFlow()

    fun loadHeroWithGuides(heroName: String): LiveData<List<VOGuideItem>> {
        return getVOHeroGuideItemsUseCase(heroName)
            .catch { _error.send(it) }
            .asLiveData()
    }

}
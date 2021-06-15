package n7.ad2.ui.heroGuide

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import n7.ad2.ui.heroGuide.domain.interactor.GetVOHeroGuideItemsInteractor
import n7.ad2.ui.heroGuide.domain.interactor.ShouldWeLoadNewHeroGuidesInteractor
import n7.ad2.ui.heroGuide.domain.vo.VOGuideItem
import javax.inject.Inject

class HeroGuideViewModel @Inject constructor(
    private val getVOHeroGuideItemsInteractor: GetVOHeroGuideItemsInteractor,
    private val shouldWeLoadNewHeroGuidesInteractor: ShouldWeLoadNewHeroGuidesInteractor,
) : ViewModel() {

    private val _error = Channel<Throwable>()
    val error = _error.receiveAsFlow()

    suspend fun shouldWeLoadNewHeroGuides(heroName: String): Boolean = shouldWeLoadNewHeroGuidesInteractor(heroName)

    fun loadHeroWithGuides(heroName: String): LiveData<List<VOGuideItem>> {
        return getVOHeroGuideItemsInteractor(heroName)
            .catch { _error.send(it) }
            .asLiveData()
    }

}
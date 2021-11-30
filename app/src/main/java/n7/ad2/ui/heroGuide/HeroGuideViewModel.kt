package n7.ad2.ui.heroGuide

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import n7.ad2.ui.heroGuide.domain.vo.VOGuideItem
import javax.inject.Inject

class HeroGuideViewModel @Inject constructor(
//    private val getVOHeroGuideItemsInteractor: GetVOHeroGuideItemsInteractor,
) : ViewModel() {

    private val _error = Channel<Throwable>(Channel.BUFFERED)
    val error = _error.receiveAsFlow()

    fun loadHeroWithGuides(heroName: String): LiveData<List<VOGuideItem>> {
        return liveData { }
//        return getVOHeroGuideItemsInteractor(heroName)
//            .catch { _error.send(it) }
//            .asLiveData()
    }

}
package n7.ad2.ui.heroGuide

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import n7.ad2.ui.heroGuide.domain.interactor.GetVOHeroGuideItemsInteractor
import n7.ad2.ui.heroGuide.domain.vo.VOGuideItem
import javax.inject.Inject

class HeroGuideViewModel @Inject constructor(
    private val getVOHeroGuideItemsInteractor: GetVOHeroGuideItemsInteractor,
) : ViewModel() {

    fun loadHeroWithGuides(heroName: String): LiveData<List<VOGuideItem>> {
        return getVOHeroGuideItemsInteractor(heroName).asLiveData()
    }

}
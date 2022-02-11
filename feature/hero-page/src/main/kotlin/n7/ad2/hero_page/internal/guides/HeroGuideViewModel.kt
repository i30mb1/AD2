package n7.ad2.hero_page.internal.guides

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import n7.ad2.android.ErrorMessage
import n7.ad2.android.ErrorMessageDelegate
import n7.ad2.hero_page.internal.guides.domain.interactor.GetVOHeroGuideItemsUseCase
import n7.ad2.hero_page.internal.guides.domain.vo.VOGuideItem

class HeroGuideViewModel @AssistedInject constructor(
    private val getVOHeroGuideItemsUseCase: GetVOHeroGuideItemsUseCase,
) : ViewModel(), ErrorMessage by ErrorMessageDelegate() {

    @AssistedFactory
    interface Factory {
        fun create(): HeroGuideViewModel
    }

    fun loadHeroWithGuides(heroName: String): LiveData<List<VOGuideItem>> {
        return getVOHeroGuideItemsUseCase(heroName)
            .catch { throwable -> showError(throwable) }
            .asLiveData()
    }

}
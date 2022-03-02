package n7.ad2.hero_page.internal.responses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import n7.ad2.AppLocale
import n7.ad2.android.ErrorMessage
import n7.ad2.android.ErrorMessageDelegate
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponse

class ResponsesViewModel @AssistedInject constructor(
    @Assisted private val heroName: String,
//    private val getHeroResponsesInteractor: GetHeroResponsesInteractor,
//    private val getLocalHeroByNameUseCase: GetLocalHeroByNameUseCase,
) : ViewModel(), ErrorMessage by ErrorMessageDelegate() {

    @AssistedFactory
    interface Factory {
        fun create(heroName: String): ResponsesViewModel
    }

    private val heroWithLocale = MutableLiveData<Pair<LocalHero, AppLocale>>()
    val voResponses: LiveData<List<VOResponse>> = heroWithLocale.switchMap {
        liveData {
//            getHeroResponsesInteractor(it.first, it.second)
//                .onSuccess {
//                    emit(it)
//                }
//                .onFailure {
//                    _error.send(it)
//                }
        }
    }

    fun refreshResponses() {
        heroWithLocale.value = heroWithLocale.value
    }

    fun loadResponses(heroName: String, heroAppLocale: AppLocale? = null) {
        viewModelScope.launch {
//            val localHero = getLocalHeroByNameUseCase(heroName)
//            val locale = heroLocale ?: Locale.valueOf(application.getString(R.string.locale))
//            heroWithLocale.value = Pair(localHero, locale)
        }
    }

}
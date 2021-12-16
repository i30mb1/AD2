package n7.ad2.hero_page.internal.responses

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import n7.ad2.android.Locale
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponse

class ResponsesViewModel @AssistedInject constructor(
    private val application: Application,
    @Assisted private val heroName: String,
//    private val getHeroResponsesInteractor: GetHeroResponsesInteractor,
//    private val getLocalHeroByNameUseCase: GetLocalHeroByNameUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(heroName: String): ResponsesViewModel
    }

    private val _error = Channel<Throwable>()
    val error = _error.receiveAsFlow()
    private val heroWithLocale = MutableLiveData<Pair<LocalHero, Locale>>()
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

    fun loadResponses(heroName: String, heroLocale: Locale? = null) {
        viewModelScope.launch {
//            val localHero = getLocalHeroByNameUseCase(heroName)
//            val locale = heroLocale ?: Locale.valueOf(application.getString(R.string.locale))
//            heroWithLocale.value = Pair(localHero, locale)
        }
    }

}
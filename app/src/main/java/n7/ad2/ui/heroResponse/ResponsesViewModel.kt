package n7.ad2.ui.heroResponse

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.data.source.local.Locale
import n7.ad2.database_heroes.api.model.LocalHero
import n7.ad2.ui.heroPage.domain.usecase.GetLocalHeroByNameUseCase
import n7.ad2.ui.heroResponse.domain.interactor.GetHeroResponsesInteractor
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import n7.ad2.utils.onFailure
import n7.ad2.utils.onSuccess
import javax.inject.Inject

class ResponsesViewModel @Inject constructor(
    private val application: Application,
    private val getHeroResponsesInteractor: GetHeroResponsesInteractor,
    private val getLocalHeroByNameUseCase: GetLocalHeroByNameUseCase,
) : ViewModel() {

    private val _error = Channel<Throwable>()
    val error = _error.receiveAsFlow()
    private val heroWithLocale = MutableLiveData<Pair<LocalHero, Locale>>()
    val voResponses: LiveData<List<VOResponse>> = heroWithLocale.switchMap {
        liveData {
            getHeroResponsesInteractor(it.first, it.second)
                .onSuccess {
                    emit(it)
                }
                .onFailure {
                    _error.send(it)
                }
        }
    }

    fun refreshResponses() {
        heroWithLocale.value = heroWithLocale.value
    }

    fun loadResponses(heroName: String, heroLocale: Locale? = null) {
        viewModelScope.launch {
            val localHero = getLocalHeroByNameUseCase(heroName)
            val locale = heroLocale ?: Locale.valueOf(application.getString(R.string.locale))
            heroWithLocale.value = Pair(localHero, locale)
        }
    }

}
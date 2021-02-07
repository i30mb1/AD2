package n7.ad2.ui.heroResponse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroPage.domain.usecase.GetLocalHeroByNameUseCase
import n7.ad2.ui.heroResponse.domain.interactor.GetHeroResponsesInteractor
import n7.ad2.utils.onFailure
import n7.ad2.utils.onSuccess
import javax.inject.Inject

class ResponsesViewModel @Inject constructor(
    application: Application,
    private val getHeroResponsesInteractor: GetHeroResponsesInteractor,
    private val getLocalHeroByNameUseCase: GetLocalHeroByNameUseCase,
) : AndroidViewModel(application) {

    private val _error = Channel<Throwable>()
    val error = _error.receiveAsFlow()
    private val heroWithLocale = MutableLiveData<Pair<LocalHero, Locale>>()
    val voResponses = heroWithLocale.switchMap {
        liveData {
            getHeroResponsesInteractor(it.first, it.second)
                .onSuccess {
                    val sourceFactory = ResponsesSourceFactory(it, "")
                    val config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(20).build()
                    emitSource(sourceFactory.toLiveData(config))
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
            val locale = heroLocale ?: Locale.valueOf(getApplication<Application>().getString(R.string.locale))
            heroWithLocale.value = Pair(localHero, locale)
        }
    }

}
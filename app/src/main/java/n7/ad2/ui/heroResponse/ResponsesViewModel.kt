package n7.ad2.ui.heroResponse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import n7.ad2.R
import n7.ad2.data.source.local.HeroLocale
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroResponse.domain.interactor.GetHeroResponsesInteractor
import javax.inject.Inject

class ResponsesViewModel @Inject constructor(
        application: Application,
        private val getHeroResponsesInteractor: GetHeroResponsesInteractor
) : AndroidViewModel(application) {

    private val _error2 = BroadcastChannel<Throwable>(Channel.BUFFERED)
    val error2 = _error2.asFlow()
    private val _error = MutableLiveData<Throwable?>()
    val error: LiveData<Throwable?> = _error
    private var locale = HeroLocale.valueOf(getApplication<Application>().getString(R.string.locale))
    private val heroName = MutableLiveData<LocalHero>()
    val voResponses = heroName.switchMap {
        liveData {
            getHeroResponsesInteractor(it, locale)
                .onSuccess {
                    val sourceFactory = ResponsesSourceFactory(it, "")
                    val config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(20).build()
                    emitSource(sourceFactory.toLiveData(config))
                }
                .onFailure {
                    _error2.send(it)
                    _error.value = it
                    _error.value = null
                }
        }
    }

    fun loadResponses(localHero: LocalHero) {
        heroName.value = localHero
    }

    fun loadResponsesLocale(locale: HeroLocale) {
        this.locale = locale
        heroName.value = heroName.value
    }

}
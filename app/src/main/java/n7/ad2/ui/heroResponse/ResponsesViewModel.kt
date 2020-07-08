package n7.ad2.ui.heroResponse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import n7.ad2.R
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroResponse.domain.interactor.GetHeroResponsesInteractor
import javax.inject.Inject

class ResponsesViewModel @Inject constructor(
        application: Application,
        private val getHeroResponsesInteractor: GetHeroResponsesInteractor
) : AndroidViewModel(application) {

    private val heroName = MutableLiveData<LocalHero>()
    val voResponses = heroName.switchMap {
        liveData {
            val sourceFactory = ResponsesSourceFactory(getHeroResponsesInteractor(it.assetsPath, getApplication<Application>().getString(R.string.language_resource)), "")
            val config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(20).build()

            emitSource(sourceFactory.toLiveData(config))
        }
    }

    fun loadResponses(localHero: LocalHero) {
        heroName.value = localHero
    }

}
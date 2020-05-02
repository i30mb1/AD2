package n7.ad2.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.ui.heroes.domain.usecase.ConvertLocalHeroListToVoListUseCase
import java.util.*
import javax.inject.Inject

class HeroesViewModel @Inject constructor(
        application: Application,
        appDatabase: AppDatabase,
        private val convertLocalHeroListToVoListUseCase: ConvertLocalHeroListToVoListUseCase
) : AndroidViewModel(application) {

    private val heroesDao = appDatabase.heroesDao
    private val heroesFilter = MutableLiveData("")
    val heroesPagedList = heroesFilter.switchMap {
        heroesDao.getHeroesFilter(it).toLiveData(10)
    }

    fun filterHeroes(chars: String) {
        heroesFilter.value = chars.toLowerCase(Locale.ENGLISH)
    }

}
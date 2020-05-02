package n7.ad2.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import n7.ad2.heroes.db.HeroModel
import n7.ad2.heroes.db.HeroesDao
import n7.ad2.heroes.db.HeroesRoomDatabase
import java.util.*

class HeroesViewModel constructor(
        application: Application
) : AndroidViewModel(application) {

    private val heroesDao: HeroesDao = HeroesRoomDatabase.getDatabase(getApplication(), null).heroesDao()
    private val heroesFilter = MutableLiveData("")
    val heroesPagedList: LiveData<PagedList<HeroModel>> = heroesFilter.switchMap {
        heroesDao.getDataSourceHeroesFilter(it).toLiveData(10)
    }

    fun filterHeroes(chars: String) {
        heroesFilter.value = chars.toLowerCase(Locale.ENGLISH)
    }

}
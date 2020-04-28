package n7.ad2.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Config
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.toLiveData
import n7.ad2.heroes.db.HeroModel
import n7.ad2.heroes.db.HeroesDao
import n7.ad2.heroes.db.HeroesRoomDatabase

class HeroesViewModel constructor(
        application: Application
) : AndroidViewModel(application) {

    private var heroesDao: HeroesDao = HeroesRoomDatabase.getDatabase(getApplication(), null).heroesDao()
    var heroes: LiveData<PagedList<HeroModel>> = heroesDao.getDataSourceHeroes().toLiveData(20)

    private fun setupLiveDataHeroes() {
        //DataSource.Factory генерирует сама Room
        val dataSource = heroesDao.getDataSourceHeroes()
        //PagedList.Config для различных условий загрузки
        val config = PagedList.Config.Builder()
                .setPageSize(30)
                //              .setInitialLoadSizeHint(60) //default = pageSize*3
                //              .setPrefetchDistance(10) //default = pageSize сколько записей до конца списка чтобы загрузить новую порцию данных
                //                .setEnablePlaceholders(true)
                .build()
        //LivePagedListBuilder создаёт PagedList в отдельном потоке (и начальную загрузку данных)
        //LivePagedListBuilder используя DataSource.Factory всегда сама может создать новую фабрику при появлении новых данных
//        heroes = LivePagedListBuilder(dataSource, config).build()
    }

    fun getHeroesByFilter(chars: String): LiveData<PagedList<HeroModel>> {
        val dataSource = heroesDao!!.getDataSourceHeroesFilter(chars.trim { it <= ' ' }.toLowerCase())
        val config = PagedList.Config.Builder().setPageSize(30).build()
        return LivePagedListBuilder(dataSource, config).build()
    }

    init {
        setupLiveDataHeroes()
    }
}
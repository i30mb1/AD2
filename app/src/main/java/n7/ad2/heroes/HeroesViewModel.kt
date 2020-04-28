package n7.ad2.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import n7.ad2.heroes.db.HeroModel
import n7.ad2.heroes.db.HeroesDao
import n7.ad2.heroes.db.HeroesRoomDatabase
import java.util.concurrent.*

class HeroesViewModel(application: Application) : AndroidViewModel(application) {

    var heroes: LiveData<PagedList<HeroModel>>? = null
        private set
    private lateinit var heroesDao: HeroesDao

    private fun setupLiveDataHeroes() {
        heroesDao = HeroesRoomDatabase.getDatabase(getApplication(), null).heroesDao()
        //DataSource.Factory генерирует сама Room
        val dataSource = heroesDao.getDataSourceHeroes()
        //PagedList.Config для различных условий загрузки
        val config = PagedList.Config.Builder()
                .setPageSize(30) //              .setInitialLoadSizeHint(60) //default = pageSize*3
                //              .setPrefetchDistance(10) //default = pageSize сколько записей до конца списка чтобы загрузить новую порцию данных
                .setEnablePlaceholders(true) //default true
                .build()
        //LivePagedListBuilder создаёт PagedList в отдельном потоке (и начальную загрузку данных)
        //LivePagedListBuilder используя DataSource.Factory всегда сама может создать новую фабрику при появлении новых данных
        heroes = LivePagedListBuilder(dataSource, config).build()
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
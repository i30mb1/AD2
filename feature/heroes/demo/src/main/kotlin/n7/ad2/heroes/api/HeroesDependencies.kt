package n7.ad2.heroes.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.Resources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.api.dao.ItemsDao
import n7.ad2.app.logger.Logger
import n7.ad2.provider.Provider

interface HeroesDependencies : Dependencies {
    val application: Application
    val res: Resources
    val itemsDao: ItemsDao
    val heroesDao: HeroesDao
    val provider: Provider
    val logger: Logger
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}
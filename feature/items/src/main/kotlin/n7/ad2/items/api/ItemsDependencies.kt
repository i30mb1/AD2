package n7.ad2.items.api

import android.app.Application
import n7.ad2.Resources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.api.dao.ItemsDao
import n7.ad2.logger.Logger
import n7.ad2.provider.Provider

interface ItemsDependencies : Dependencies {
    val application: Application
    val res: Resources
    val itemsDao: ItemsDao
    val heroesDao: HeroesDao
    val provider: Provider
    val logger: Logger
    val dispatchersProvider: DispatchersProvider
}
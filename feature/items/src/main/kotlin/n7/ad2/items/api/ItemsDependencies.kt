package n7.ad2.items.api

import android.app.Application
import n7.ad2.AppResources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.api.dao.ItemsDao
import n7.ad2.logger.AD2Logger
import n7.ad2.provider.Provider

interface ItemsDependencies : Dependencies {
    val application: Application
    val res: AppResources
    val itemsDao: ItemsDao
    val heroesDao: HeroesDao
    val provider: Provider
    val logger: AD2Logger
    val dispatchersProvider: DispatchersProvider
}
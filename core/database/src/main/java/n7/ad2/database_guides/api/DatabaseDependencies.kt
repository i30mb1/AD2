package n7.ad2.database_guides.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.android.Dependencies
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.database_guides.api.dao.ItemsDao
import n7.ad2.logger.AD2Logger

interface DatabaseDependencies : Dependencies {
    val heroesDao: HeroesDao
    val itemsDao: ItemsDao
    val application: Application
    val logger: AD2Logger
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}
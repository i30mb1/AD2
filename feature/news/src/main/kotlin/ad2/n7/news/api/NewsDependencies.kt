package ad2.n7.news.api

import android.app.Application
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.database_guides.api.dao.NewsDao
import n7.ad2.logger.Logger
import n7.ad2.provider.Provider

interface NewsDependencies : Dependencies {
    val application: Application
    val logger: Logger
    val provider: Provider
    val newsDao: NewsDao
    val dispatchersProvider: DispatchersProvider
}
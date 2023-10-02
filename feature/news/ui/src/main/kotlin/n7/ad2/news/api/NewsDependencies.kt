package n7.ad2.news.api

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.app.logger.Logger
import n7.ad2.apppreference.Preference
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.database_guides.api.AppDatabase
import n7.ad2.database_guides.api.dao.NewsDao
import n7.ad2.navigator.Navigator

interface NewsDependencies : Dependencies {
    val application: Application
    val logger: Logger
    val navigator: Navigator
    val preference: Preference
    val appDatabase: AppDatabase
    val newsDao: NewsDao
    val appInformation: AppInformation
    val dispatchersProvider: DispatchersProvider
}
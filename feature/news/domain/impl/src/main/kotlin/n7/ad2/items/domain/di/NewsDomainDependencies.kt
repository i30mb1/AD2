package n7.ad2.items.domain.di

import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.api.dao.NewsDao

interface NewsDomainDependencies {
    val newsDao: NewsDao
    val logger: Logger
    val res: Resources
    val appInformation: AppInformation
    val dispatcher: DispatchersProvider
}

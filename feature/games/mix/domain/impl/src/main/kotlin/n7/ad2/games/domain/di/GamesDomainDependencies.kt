package n7.ad2.games.domain.di

import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider

interface GamesDomainDependencies {
    val logger: Logger
    val res: Resources
    val appInformation: AppInformation
    val dispatcher: DispatchersProvider
}

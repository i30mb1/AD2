package n7.ad2.feature.games.xo.domain.di

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider

interface XoDomainDependencies {
    val logger: Logger
    val application: Application
    val res: Resources
    val appInformation: AppInformation
    val dispatcher: DispatchersProvider
}

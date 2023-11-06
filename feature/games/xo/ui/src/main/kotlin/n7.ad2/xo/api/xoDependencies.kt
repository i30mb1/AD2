package n7.ad2.xo.api

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.feature.games.xo.domain.Client
import n7.ad2.feature.games.xo.domain.Server

interface XoDependencies : Dependencies {
    val application: Application
    val res: Resources
    val logger: Logger
    val dispatchersProvider: DispatchersProvider
    val appInformation: AppInformation
}

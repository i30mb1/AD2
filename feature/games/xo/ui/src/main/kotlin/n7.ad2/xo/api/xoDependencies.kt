package n7.ad2.xo.api

import android.app.Application
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies

interface XoDependencies : Dependencies {
    val application: Application
    val res: Resources
    val logger: Logger
    val dispatchersProvider: DispatchersProvider
}

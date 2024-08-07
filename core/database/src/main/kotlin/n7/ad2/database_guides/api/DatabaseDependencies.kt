package n7.ad2.database_guides.api

import android.app.Application
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies

interface DatabaseDependencies : Dependencies {
    val application: Application
    val res: Resources
    val logger: Logger
    val dispatchersProvider: DispatchersProvider
}

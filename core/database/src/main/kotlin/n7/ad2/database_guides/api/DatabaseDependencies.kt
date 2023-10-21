package n7.ad2.database_guides.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies

interface DatabaseDependencies : Dependencies {
    val application: Application
    val res: Resources
    val logger: Logger
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}

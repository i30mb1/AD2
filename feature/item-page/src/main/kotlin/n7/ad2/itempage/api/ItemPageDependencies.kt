package n7.ad2.itempage.api

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.navigator.Navigator

interface ItemPageDependencies : Dependencies {
    val application: Application
    val res: Resources
    val appInfo: AppInformation
    val navigator: Navigator
    val logger: Logger
    val dispatchersProvider: DispatchersProvider
}

package n7.ad2.items.domian.di

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider

interface ItemsDomainDependencies {
    val res: Resources
    val appInformation: AppInformation
    val dispatcher: DispatchersProvider
    val application: Application
    val logger: Logger
}

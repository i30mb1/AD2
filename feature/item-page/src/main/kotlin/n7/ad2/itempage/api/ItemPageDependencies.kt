package n7.ad2.itempage.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.logger.Logger
import n7.ad2.provider.Provider
import n7.ad2.repositories.ItemRepository

interface ItemPageDependencies : Dependencies {
    val application: Application
    val res: Resources
    val appInfo: AppInformation
    val provider: Provider
    val itemRepository: ItemRepository
    val logger: Logger
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}
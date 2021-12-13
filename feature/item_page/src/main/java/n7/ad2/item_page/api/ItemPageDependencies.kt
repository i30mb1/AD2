package n7.ad2.item_page.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.android.Dependencies
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.logger.AD2Logger
import n7.ad2.provider.Provider
import n7.ad2.repositories.ItemRepository

interface ItemPageDependencies : Dependencies {
    val application: Application
    val provider: Provider
    val itemRepository: ItemRepository
    val logger: AD2Logger
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}
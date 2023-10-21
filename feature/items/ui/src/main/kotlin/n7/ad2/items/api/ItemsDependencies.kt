package n7.ad2.items.api

import android.app.Application
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.items.domain.usecase.FilterItemsUseCase
import n7.ad2.items.domain.usecase.GetItemsUseCase
import n7.ad2.items.domain.usecase.UpdateItemViewedForItemUseCase
import n7.ad2.navigator.Navigator

interface ItemsDependencies : Dependencies {
    val application: Application
    val res: Resources
    val navigator: Navigator
    val logger: Logger
    val dispatchersProvider: DispatchersProvider
    val getItemsUseCase: GetItemsUseCase
    val filterItemsUseCase: FilterItemsUseCase
    val updateItemViewedForItemUseCase: UpdateItemViewedForItemUseCase
}

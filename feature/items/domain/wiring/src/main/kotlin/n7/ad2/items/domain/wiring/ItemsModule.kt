package n7.ad2.items.domain.wiring

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.items.domain.usecase.FilterItemsUseCase
import n7.ad2.items.domain.usecase.GetItemsUseCase
import n7.ad2.items.domain.usecase.UpdateItemViewedForItemUseCase
import n7.ad2.items.domian.di.ItemsDomainComponent
import n7.ad2.items.domian.di.ItemsDomainDependencies

@dagger.Module
object ItemsModule {

    @dagger.Provides
    fun provideHeroesDomainComponent(res: Resources, dispatchers: DispatchersProvider, appInformation: AppInformation, application: Application, logger: Logger): ItemsDomainComponent = ItemsDomainComponent(
        object : ItemsDomainDependencies {
            override val application: Application = application
            override val logger = logger
            override val res: Resources = res
            override val dispatcher = dispatchers
            override val appInformation = appInformation
        },
    )

    @dagger.Provides
    fun provideFilterItemsUseCase(component: ItemsDomainComponent): FilterItemsUseCase = component.filterItemsUseCase

    @dagger.Provides
    fun provideGetItemsUseCase(component: ItemsDomainComponent): GetItemsUseCase = component.getItemsUseCase

    @dagger.Provides
    fun provideUpdateItemViewedForItemUseCase(component: ItemsDomainComponent): UpdateItemViewedForItemUseCase = component.updateItemViewedForItemUseCase
}

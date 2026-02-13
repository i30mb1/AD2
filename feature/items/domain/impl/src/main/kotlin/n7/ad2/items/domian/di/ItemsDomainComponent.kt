package n7.ad2.items.domian.di

import n7.ad2.items.domain.usecase.FilterItemsUseCase
import n7.ad2.items.domain.usecase.GetItemsUseCase
import n7.ad2.items.domain.usecase.UpdateItemViewedForItemUseCase
import n7.ad2.items.domian.internal.data.ItemsRepositoryImpl
import n7.ad2.items.domian.internal.data.db.ItemsDatabase
import n7.ad2.items.domian.internal.usecase.FilterItemsUseCaseImpl
import n7.ad2.items.domian.internal.usecase.GetItemsUseCaseImpl
import n7.ad2.items.domian.internal.usecase.UpdateItemViewedForItemUseCaseImpl

interface ItemsDomainComponent {

    val filterItemsUseCase: FilterItemsUseCase
    val getItemsUseCase: GetItemsUseCase
    val updateItemViewedForItemUseCase: UpdateItemViewedForItemUseCase
}

fun ItemsDomainComponent(dependencies: ItemsDomainDependencies): ItemsDomainComponent = object : ItemsDomainComponent {

    private val itemDatabase = ItemsDatabase.getInstance(dependencies.application)
    private val itemDao = itemDatabase.itemsDao
    private val itemsRepository = ItemsRepositoryImpl(itemDao)

    override val filterItemsUseCase = FilterItemsUseCaseImpl(
        dependencies.dispatcher,
    )

    override val getItemsUseCase = GetItemsUseCaseImpl(
        itemsRepository,
    )

    override val updateItemViewedForItemUseCase = UpdateItemViewedForItemUseCaseImpl(
        itemsRepository,
        dependencies.dispatcher,
    )
}

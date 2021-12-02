package n7.ad2.items.internal.di

import n7.ad2.items.api.ItemsDependencies
import n7.ad2.items.internal.ItemsFragment
import n7.ad2.items.internal.ItemsViewModel

@dagger.Component(
    dependencies = [
        ItemsDependencies::class
    ]
)
internal interface ItemsComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: ItemsDependencies): ItemsComponent
    }

    fun inject(itemsFragment: ItemsFragment)

    val itemsViewModel: ItemsViewModel.Factory

}
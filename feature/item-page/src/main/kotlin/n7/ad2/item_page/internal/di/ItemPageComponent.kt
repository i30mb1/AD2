package n7.ad2.item_page.internal.di

import n7.ad2.item_page.api.ItemPageDependencies
import n7.ad2.item_page.internal.ItemInfoFragment
import n7.ad2.item_page.internal.ItemInfoViewModel

@dagger.Component(
    dependencies = [
        ItemPageDependencies::class,
    ],
)
interface ItemPageComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: ItemPageDependencies): ItemPageComponent
    }

    fun inject(drawerFragment: ItemInfoFragment)

    val itemInfoViewModelFactory: ItemInfoViewModel.Factory

}
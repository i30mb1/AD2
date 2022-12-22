package n7.ad2.itempage.internal.di

import n7.ad2.itempage.api.ItemPageDependencies
import n7.ad2.itempage.internal.ItemInfoFragment
import n7.ad2.itempage.internal.ItemInfoViewModel

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
package n7.ad2.drawer.internal.di

import dagger.Component
import n7.ad2.drawer.api.DrawerDependencies
import n7.ad2.drawer.internal.DrawerFragment
import n7.ad2.drawer.internal.DrawerViewModel

@Component(
    dependencies = [
        DrawerDependencies::class,
    ],
    modules = [
        DrawerModule::class,
    ],
)
internal interface DrawerComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: DrawerDependencies): DrawerComponent
    }

    fun inject(drawerFragment: DrawerFragment)

    val drawerViewModel: DrawerViewModel.Factory
}

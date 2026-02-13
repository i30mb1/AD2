package n7.ad2.heroes.ui.internal.di

import n7.ad2.heroes.ui.api.HeroesDependencies
import n7.ad2.heroes.ui.internal.HeroesFragment
import n7.ad2.heroes.ui.internal.HeroesViewModel

@dagger.Component(
    dependencies = [
        HeroesDependencies::class,
    ],
)
internal interface HeroesComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: HeroesDependencies): HeroesComponent
    }

    fun inject(drawerFragment: HeroesFragment)

    val heroesViewModelFactory: HeroesViewModel.Factory
}

package n7.ad2.heroes.internal.di

import n7.ad2.heroes.api.HeroesDependencies
import n7.ad2.heroes.internal.HeroesFragment
import n7.ad2.heroes.internal.HeroesViewModel

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

    val heroesViewModel: HeroesViewModel.Factory

}
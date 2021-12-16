package n7.ad2.games.internal.di

import n7.ad2.games.api.GamesDependencies
import n7.ad2.games.internal.GamesFragment
import n7.ad2.games.internal.GamesViewModel

@dagger.Component(
    dependencies = [
        GamesDependencies::class
    ],
)
internal interface GamesComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: GamesDependencies): GamesComponent
    }

    fun inject(gamesFragment: GamesFragment)

    val gamesViewModelFactory: GamesViewModel.Factory

}
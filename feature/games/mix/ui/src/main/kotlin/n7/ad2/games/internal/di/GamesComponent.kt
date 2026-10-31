package n7.ad2.games.internal.di

import n7.ad2.feature.games.xo.wiring.XoModule
import n7.ad2.games.api.GamesDependencies
import n7.ad2.games.internal.GamesFragment
import n7.ad2.games.internal.GamesViewModel
import n7.ad2.games.internal.games.invoker.InvokerFragment
import n7.ad2.games.internal.games.mid.MidFragment
import n7.ad2.games.internal.games.show.ShowFragment
import n7.ad2.games.internal.leaderboard.LeaderboardFragment
import n7.ad2.games.wiring.GamesModule

@dagger.Component(
    dependencies = [
        GamesDependencies::class,
    ],
    modules = [
        XoModule::class,
        GamesModule::class,
    ],
)
internal interface GamesComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: GamesDependencies): GamesComponent
    }

    fun inject(gamesFragment: GamesFragment)
    fun inject(midFragment: MidFragment)
    fun inject(invokerFragment: InvokerFragment)
    fun inject(showFragment: ShowFragment)
    fun inject(leaderboardFragment: LeaderboardFragment)

    val gamesViewModelFactory: GamesViewModel.Factory
}

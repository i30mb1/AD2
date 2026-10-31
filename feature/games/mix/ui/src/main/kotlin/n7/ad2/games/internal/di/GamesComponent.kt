package n7.ad2.games.internal.di

import n7.ad2.feature.games.xo.wiring.XoModule
import n7.ad2.games.api.GamesDependencies
import n7.ad2.games.internal.GamesFragment
import n7.ad2.games.internal.GamesViewModel
import n7.ad2.games.internal.games.killCreep.KillCreepFragment
import n7.ad2.games.internal.games.skillmp.SkillGameFragment
import n7.ad2.games.internal.leaderboard.LeaderboardFragment
import n7.ad2.games.internal.lobby.LobbyFragment
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
    fun inject(killCreepFragment: KillCreepFragment)
    fun inject(gameGuessTheSkillManaPoint: SkillGameFragment)
    fun inject(lobbyFragment: LobbyFragment)
    fun inject(leaderboardFragment: LeaderboardFragment)

    val gamesViewModelFactory: GamesViewModel.Factory
}

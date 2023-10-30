package n7.ad2.games.internal.di

import n7.ad2.games.api.GamesDependencies
import n7.ad2.games.internal.GamesFragment
import n7.ad2.games.internal.GamesViewModel
import n7.ad2.games.internal.games.killCreep.KillCreepFragment
import n7.ad2.games.internal.games.skillmp.SkillGameFragment
import n7.ad2.games.internal.games.xo.XOGameFragment

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
    fun inject(xoGameFragment: XOGameFragment)
    fun inject(killCreepFragment: KillCreepFragment)
    fun inject(gameGuessTheSkillManaPoint: SkillGameFragment)

    val gamesViewModelFactory: GamesViewModel.Factory

}

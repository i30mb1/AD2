package n7.ad2.games.domain.di

import n7.ad2.games.domain.internal.usecase.GameServerImpl
import n7.ad2.games.domain.usecase.GameServer

interface GamesDomainComponent {

    val gameServer: GameServer
}

fun GamesDomainComponent(
    dependencies: GamesDomainDependencies,
): GamesDomainComponent = object : GamesDomainComponent {
    override val gameServer: GameServer
        get() = GameServerImpl(TODO())
}

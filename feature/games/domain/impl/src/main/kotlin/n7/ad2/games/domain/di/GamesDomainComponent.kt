package n7.ad2.games.domain.di

import n7.ad2.games.domain.Client
import n7.ad2.games.domain.Server
import n7.ad2.games.domain.internal.server.base.ClientSocketProxy
import n7.ad2.games.domain.internal.server.base.ServerSocketProxy
import n7.ad2.games.domain.internal.server.socket.ClientWithSocket
import n7.ad2.games.domain.internal.server.socket.ServerWithSocket

interface GamesDomainComponent {
    val server: Server
    val client: Client
}

fun GamesDomainComponent(
    dependencies: GamesDomainDependencies,
): GamesDomainComponent = object : GamesDomainComponent {
    override val server: Server = ServerWithSocket(ServerSocketProxy())
    override val client: Client = ClientWithSocket(ClientSocketProxy())
}

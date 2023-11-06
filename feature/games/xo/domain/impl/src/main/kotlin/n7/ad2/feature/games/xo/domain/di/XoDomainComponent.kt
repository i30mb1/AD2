package n7.ad2.feature.games.xo.domain.di

import n7.ad2.feature.games.xo.domain.Client
import n7.ad2.feature.games.xo.domain.Server
import n7.ad2.feature.games.xo.domain.internal.server.base.ClientSocketProxy
import n7.ad2.feature.games.xo.domain.internal.server.base.ServerSocketProxy
import n7.ad2.feature.games.xo.domain.internal.server.socket.ClientWithSocket
import n7.ad2.feature.games.xo.domain.internal.server.socket.ServerWithSocket

interface XoDomainComponent {
    val server: Server
    val client: Client
}

fun XoDomainComponent(
    dependencies: XoDomainDependencies,
): XoDomainComponent = object : XoDomainComponent {
    override val server: Server = ServerWithSocket(ServerSocketProxy())
    override val client: Client = ClientWithSocket(ClientSocketProxy())
}

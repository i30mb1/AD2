package n7.ad2.feature.games.xo.domain.internal.server.controller

import kotlinx.coroutines.flow.StateFlow
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerState
import java.net.InetAddress

interface ServerController {
    val state: StateFlow<ServerState>
    fun start(name: String, ip: InetAddress = InetAddress.getLoopbackAddress(), port: Int = 0)
    fun send(text: String)
    fun stop()
}

package n7.ad2.feature.games.xo.domain.internal.server2

import java.net.InetAddress
import kotlinx.coroutines.flow.StateFlow
import n7.ad2.feature.games.xo.domain.internal.server2.data.ServerState

interface ServerController {
    val state: StateFlow<ServerState>
    fun start(name: String, ip: InetAddress = InetAddress.getLoopbackAddress(), port: Int = 0)
    fun send(text: String)
    fun stop()
}

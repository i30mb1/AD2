package n7.ad2.feature.games.xo.domain.internal.server.controller

import kotlinx.coroutines.flow.StateFlow
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientState
import java.net.InetAddress

interface ClientController {
    val state: StateFlow<ClientState>
    fun connect(name: String, ip: InetAddress, port: Int)
    fun send(text: String)
    fun disconnect()
}

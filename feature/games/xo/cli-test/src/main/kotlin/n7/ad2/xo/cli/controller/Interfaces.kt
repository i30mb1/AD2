package n7.ad2.xo.cli.controller

import java.net.InetAddress
import kotlinx.coroutines.flow.StateFlow
import n7.ad2.xo.cli.model.ClientState
import n7.ad2.xo.cli.model.ServerState

interface CliServerController {
    val state: StateFlow<ServerState>
    suspend fun start(name: String, ip: InetAddress = InetAddress.getLoopbackAddress(), port: Int = 0)
    suspend fun send(text: String)
    fun stop()

    companion object {
        fun create(socketType: n7.ad2.xo.cli.model.SocketType): CliServerController {
            return when (socketType) {
                n7.ad2.xo.cli.model.SocketType.RAW -> RawSocketServerController()
                n7.ad2.xo.cli.model.SocketType.HTTP -> HttpServerController()
                n7.ad2.xo.cli.model.SocketType.WEBSOCKET -> WebSocketServerController()
            }
        }
    }
}

interface CliClientController {
    val state: StateFlow<ClientState>
    suspend fun connect(name: String, ip: InetAddress, port: Int)
    suspend fun send(text: String)
    fun disconnect()

    companion object {
        fun create(socketType: n7.ad2.xo.cli.model.SocketType): CliClientController {
            return when (socketType) {
                n7.ad2.xo.cli.model.SocketType.RAW -> RawSocketClientController()
                n7.ad2.xo.cli.model.SocketType.HTTP -> HttpClientController()
                n7.ad2.xo.cli.model.SocketType.WEBSOCKET -> WebSocketClientController()
            }
        }
    }
}

package n7.ad2.feature.games.xo.domain.model

import java.net.ServerSocket

interface Server {
    val name: String
    val ip: String
    val port: Int
}

open class SimpleServer(
    override val name: String,
    override val ip: String,
    override val port: Int,
) : Server

open class WifiDirectServer(
    override val name: String,
    override val ip: String,
    override val port: Int,
) : Server

data class SimpleSocketServer(
    val serverSocket: ServerSocket,
    override val name: String,
    override val ip: String,
    override val port: Int,
) : SimpleServer(name, ip, port)
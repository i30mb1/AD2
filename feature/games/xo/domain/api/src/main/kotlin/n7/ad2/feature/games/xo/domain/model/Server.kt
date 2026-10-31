package n7.ad2.feature.games.xo.domain.model

import java.net.ServerSocket

interface Server {
    val name: String
    val ip: String
    val port: Int
}

open class SimpleServer(override val name: String, override val ip: String, override val port: Int) : Server

data class SocketServerModel(val serverSocket: ServerSocket, override val name: String, override val ip: String, override val port: Int) : SimpleServer(name, ip, port)

/** Комната, найденная через Wi-Fi Direct: ip хоста станет известен только после входа в его группу */
data class WifiDirectRoom(val name: String, val deviceAddress: String, val port: Int)

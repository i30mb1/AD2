package n7.ad2.feature.games.xo.domain

import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

interface ServerHolder {
    suspend fun start(host: InetAddress, name: String): ServerSocket
    suspend fun awaitClient(): Socket
}

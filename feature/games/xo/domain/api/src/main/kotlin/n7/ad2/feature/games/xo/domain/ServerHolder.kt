package n7.ad2.feature.games.xo.domain

import java.net.InetAddress
import java.net.Socket
import n7.ad2.feature.games.xo.domain.model.SimpleSocketServer

interface ServerHolder {
    suspend fun start(host: InetAddress, name: String): SimpleSocketServer
    suspend fun awaitClient(): Socket
    suspend fun close()
}

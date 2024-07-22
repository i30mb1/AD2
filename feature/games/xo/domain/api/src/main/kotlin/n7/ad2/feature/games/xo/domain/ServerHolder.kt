package n7.ad2.feature.games.xo.domain

import java.net.InetAddress
import java.net.Socket
import n7.ad2.feature.games.xo.domain.model.Server

interface ServerHolder {
    suspend fun start(host: InetAddress, name: String): Server
    suspend fun awaitClient(): Socket
    suspend fun close()
}

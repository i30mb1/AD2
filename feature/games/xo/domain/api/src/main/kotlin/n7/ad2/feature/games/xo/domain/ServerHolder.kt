package n7.ad2.feature.games.xo.domain

import java.net.InetAddress

interface ServerHolder {
    suspend fun start(host: InetAddress, name: String)
    suspend fun awaitMessage(): String
    suspend fun awaitClient()
    fun sendMessage(message: String)
}

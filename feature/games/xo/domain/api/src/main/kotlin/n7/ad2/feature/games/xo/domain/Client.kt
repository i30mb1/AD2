package n7.ad2.feature.games.xo.domain

import java.net.InetAddress

interface Client {
    suspend fun start(host: InetAddress?, port: Int)
    suspend fun awaitMessage(): String
    fun sendMessage(message: String)
}

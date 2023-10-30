package n7.ad2.games.domain

import java.net.InetAddress

interface Client {
    suspend fun start(host: InetAddress?, port: Int)
    suspend fun awaitMessage(): String
    fun sendMessage(message: String)
}

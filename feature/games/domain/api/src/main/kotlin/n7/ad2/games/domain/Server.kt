package n7.ad2.games.domain

import java.net.InetAddress

interface Server {
    suspend fun start(host: InetAddress, ports: IntArray)
    suspend fun awaitMessage(): String
    suspend fun awaitClient()
    fun sendMessage(message: String)
}

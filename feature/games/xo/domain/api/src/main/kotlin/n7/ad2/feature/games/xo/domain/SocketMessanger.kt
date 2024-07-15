package n7.ad2.feature.games.xo.domain

import java.net.Socket

interface SocketMessanger {
    fun init(socket: Socket)
    fun isConnected(): Boolean
    suspend fun awaitMessage(): String
    fun sendMessage(message: String)
}

package n7.ad2.feature.games.xo.domain

import java.net.Socket

interface SocketMessanger {
    val socket: Socket
    suspend fun awaitMessage(): String
    fun sendMessage(message: String)
}

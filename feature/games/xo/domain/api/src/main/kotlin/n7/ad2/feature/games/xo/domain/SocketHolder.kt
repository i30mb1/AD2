package n7.ad2.feature.games.xo.domain

import java.net.Socket

interface SocketHolder {
    var socket: Socket?
    suspend fun awaitMessage(): String
    fun sendMessage(message: String)
}

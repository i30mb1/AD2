package n7.ad2.feature.games.xo.domain

import n7.ad2.feature.games.xo.domain.model.SocketServerModel

interface SocketMessenger {
    fun init(server: SocketServerModel)
    fun isConnected(): Boolean
    suspend fun awaitMessage(): String?
    fun sendMessage(message: String)
}

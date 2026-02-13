package n7.ad2.feature.games.xo.domain

import n7.ad2.feature.games.xo.domain.model.SocketServerModel
import java.net.InetAddress

interface ServerCreator {
    suspend fun create(name: String, host: InetAddress, port: Int = 0): SocketServerModel
}

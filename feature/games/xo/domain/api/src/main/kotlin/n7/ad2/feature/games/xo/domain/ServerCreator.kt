package n7.ad2.feature.games.xo.domain

import java.net.InetAddress
import n7.ad2.feature.games.xo.domain.model.SocketServerModel

interface ServerCreator {
    suspend fun create(name: String, host: InetAddress, port: Int = 0): SocketServerModel
}

package n7.ad2.feature.games.xo.domain

import java.net.InetAddress
import java.net.Socket

interface ClientCreator {
    suspend fun create(host: InetAddress?, port: Int): Socket
}

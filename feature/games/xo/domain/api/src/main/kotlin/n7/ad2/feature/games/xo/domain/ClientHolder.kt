package n7.ad2.feature.games.xo.domain

import java.net.InetAddress
import java.net.Socket

interface ClientHolder {
    suspend fun start(host: InetAddress?, port: Int): Socket
}

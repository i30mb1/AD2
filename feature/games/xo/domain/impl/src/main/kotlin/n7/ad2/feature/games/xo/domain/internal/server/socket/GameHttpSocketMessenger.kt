package n7.ad2.feature.games.xo.domain.internal.server.socket

import java.io.PrintWriter
import java.net.Socket
import java.util.Scanner
import n7.ad2.feature.games.xo.domain.SocketMessanger

internal class GameHttpSocketMessenger : SocketMessanger {

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var scanner: Scanner? = null

    override fun init(socket: Socket) {
        this.socket = socket
        writer = PrintWriter(socket.getOutputStream())
        scanner = Scanner(socket.getInputStream())
    }

    override fun isConnected(): Boolean {
        return socket != null
    }

    override suspend fun awaitMessage(): String? {
        TODO("Not yet implemented")
    }

    override fun sendMessage(message: String) {
        TODO("Not yet implemented")
    }
}

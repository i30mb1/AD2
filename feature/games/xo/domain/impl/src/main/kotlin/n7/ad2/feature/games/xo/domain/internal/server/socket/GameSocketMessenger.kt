package n7.ad2.feature.games.xo.domain.internal.server.socket

import java.io.PrintWriter
import java.net.Socket
import java.util.Scanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import n7.ad2.feature.games.xo.domain.SocketMessanger

class GameSocketMessenger : SocketMessanger {

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

    override fun sendMessage(message: String) {
        val writer = writer ?: error("call init first")
        writer.println(message)
        writer.flush()
    }

    override suspend fun awaitMessage(): String? = messages().firstOrNull()

    private fun messages(): Flow<String> = flow {
        val scanner = scanner ?: error("call init first")
        while (scanner.hasNext()) {
            val message = scanner.nextLine()
            emit(message)
        }
        scanner.close()
        socket?.close()
        this@GameSocketMessenger.socket = null
    }
}

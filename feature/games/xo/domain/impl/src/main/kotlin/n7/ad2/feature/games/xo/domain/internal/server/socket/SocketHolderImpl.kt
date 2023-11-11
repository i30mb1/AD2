package n7.ad2.feature.games.xo.domain.internal.server.socket

import java.io.PrintWriter
import java.net.Socket
import java.util.Scanner
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.feature.games.xo.domain.SocketHolder

class SocketHolderImpl : SocketHolder {

    override var socket: Socket? = null

    override fun sendMessage(message: String) {
        val output = requireNotNull(socket).getOutputStream()
        val writer = PrintWriter(output)
        writer.print("$message\n")
        writer.flush()
    }

    override suspend fun awaitMessage(): String = suspendCancellableCoroutine { continuation ->
        val input = requireNotNull(socket).getInputStream()
        val scanner = Scanner(input)
        continuation.resume(scanner.nextLine())
    }

//    private suspend fun handleClient(socket: Socket) = scope.launch {
//        val scanner = Scanner(socket.getInputStream())
//        while (socket.isConnected) {
//            val message = scanner.nextLine()
//            messages.send(message)
//        }
//        events.send(ServerHolderWithSocket.ServerWithSocketEvents.Closed)
//    }
}

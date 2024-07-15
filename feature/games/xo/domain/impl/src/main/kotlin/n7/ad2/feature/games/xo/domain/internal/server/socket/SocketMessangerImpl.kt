package n7.ad2.feature.games.xo.domain.internal.server.socket

import java.io.PrintWriter
import java.net.Socket
import java.util.Scanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import n7.ad2.feature.games.xo.domain.SocketMessanger

class SocketMessangerImpl(
    override val socket: Socket,
) : SocketMessanger {

    override fun sendMessage(message: String) {
        val output = socket.getOutputStream()
        val writer = PrintWriter(output)
        writer.print("$message\n")
        writer.flush()
        writer.close()
    }

    override suspend fun awaitMessage(): String = messages().first()

    private fun messages(): Flow<String> = flow {
        val scanner = Scanner(socket.getInputStream())
        while (socket.isConnected) {
            val message = scanner.nextLine()
            emit(message)
        }
        scanner.close()
    }
}

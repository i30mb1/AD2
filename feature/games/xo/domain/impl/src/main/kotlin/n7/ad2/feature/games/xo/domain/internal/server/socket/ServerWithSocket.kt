package n7.ad2.feature.games.xo.domain.internal.server.socket

import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.Scanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import n7.ad2.feature.games.xo.domain.Server
import n7.ad2.feature.games.xo.domain.internal.server.base.ServerSocketProxy

internal class ServerWithSocket(
    private val serverSocketProxy: ServerSocketProxy,
) : Server {
    sealed interface ServerWithSocketEvents {
        object Started : ServerWithSocketEvents
        object ClientConnected : ServerWithSocketEvents
        object Closed : ServerWithSocketEvents
    }

    private val scope = CoroutineScope(Job())
    private val messages = Channel<String>()
    private val events = Channel<ServerWithSocketEvents>(Channel.BUFFERED)
    private var server: ServerSocket? = null
    private var socket: Socket? = null

    override suspend fun start(
        host: InetAddress,
        ports: IntArray,
    ) {
        server = serverSocketProxy.getServerSocket(host, ports)
        events.send(ServerWithSocketEvents.Started)
    }

    override suspend fun awaitClient() {
        val server = requireNotNull(server)
        socket = serverSocketProxy.getClientSocket(server)
        events.send(ServerWithSocketEvents.ClientConnected)
        handleClient(requireNotNull(socket))
    }

    override fun sendMessage(message: String) {
        val socket = socket ?: kotlin.run {
            error("")
        }
        val writer = PrintWriter(socket.getOutputStream())
        writer.print("$message\n")
        writer.flush()
    }

    private suspend fun handleClient(socket: Socket) = scope.launch {
        val scanner = Scanner(socket.getInputStream())
        while (socket.isConnected) {
            val message = scanner.nextLine()
            messages.send(message)
        }
        events.send(ServerWithSocketEvents.Closed)
    }

    override suspend fun awaitMessage(): String {
        return messages.receive()
    }
}

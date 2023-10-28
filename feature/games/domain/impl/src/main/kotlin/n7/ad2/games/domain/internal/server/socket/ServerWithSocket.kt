package n7.ad2.games.domain.internal.server.socket

import java.net.InetAddress
import java.net.Socket
import java.util.Scanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.games.domain.internal.server.base.ServerSocketProxy

class ServerWithSocket(
    private val serverSocketProxy: ServerSocketProxy,
    private val dispatcher: DispatchersProvider,
) {
    sealed interface ServerWithSocketEvents {
        object Started : ServerWithSocketEvents
        object ClientConnected : ServerWithSocketEvents
        object Closed : ServerWithSocketEvents
    }

    private val scope = CoroutineScope(Job())
    private val messages = Channel<String>()
    private val events = Channel<ServerWithSocketEvents>(Channel.BUFFERED)

    fun start(
        host: InetAddress,
        ports: IntArray,
    ) = scope.launch {
        val server = serverSocketProxy.getServerSocket(host, ports)
        events.send(ServerWithSocketEvents.Started)
        val clientSocket = serverSocketProxy.getClientSocket(server)
        events.send(ServerWithSocketEvents.ClientConnected)
        handleClient(clientSocket)
        events.send(ServerWithSocketEvents.Closed)
    }

    suspend fun awaitStart() {
        val event = events.receive()
        event is ServerWithSocketEvents.Started
    }

    suspend fun awaitClient() {
        val event = events.receive()
        event is ServerWithSocketEvents.ClientConnected
    }

    private suspend fun handleClient(socket: Socket) {
        val scanner = Scanner(socket.getInputStream())
        while (socket.isConnected) {
            val message = scanner.nextLine()
            messages.send(message)
        }
    }

    suspend fun awaitMessage(): String {
        return messages.receive()
    }
}

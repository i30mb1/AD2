package n7.ad2.games.domain.internal.server.socket

import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.util.Scanner
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.games.domain.internal.server.ClientLog
import n7.ad2.games.domain.internal.server.base.ClientSocketProxy

class ClientWithSocket(
    private val clientSocketProxy: ClientSocketProxy,
) {
    sealed interface ClientWithSocketEvents {
        object Started : ClientWithSocketEvents
        object ServerConnected : ClientWithSocketEvents
        object Closed : ClientWithSocketEvents
    }

    val logger: (message: ClientLog) -> Unit = { }

    private val scope = CoroutineScope(Job())
    private val incomingMessages = Channel<String>()
    private val events = Channel<ClientWithSocketEvents>(Channel.BUFFERED)
    private var socket: Socket? = null

    suspend fun start(
        host: InetAddress?,
        port: Int,
    ) {
        socket = clientSocketProxy.start(host, port)
        events.send(ClientWithSocketEvents.Started)
    }

    fun sendMessage(message: String) {
        val socket = socket ?: kotlin.run {
            logger(ClientLog.ClientSendingMessageError)
            error("")
        }
        val writer = PrintWriter(socket.getOutputStream())
        writer.print("$message\n")
        writer.flush()
    }

    suspend fun awaitMessage(): String = suspendCancellableCoroutine { continuation ->
        val inputStream = socket!!.getInputStream()
        val scanner = Scanner(inputStream)
        continuation.resume(scanner.nextLine())
    }
}

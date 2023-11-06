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
import n7.ad2.games.domain.Client
import n7.ad2.games.domain.internal.server.ClientLog
import n7.ad2.games.domain.internal.server.base.ClientSocketProxy

internal class ClientWithSocket(
    private val clientSocketProxy: ClientSocketProxy,
) : Client {

    val logger: (message: ClientLog) -> Unit = { }

    private val scope = CoroutineScope(Job())
    private val incomingMessages = Channel<String>()
    private var socket: Socket? = null

    override suspend fun start(
        host: InetAddress?,
        port: Int,
    ) {
        socket = clientSocketProxy.start(host, port)
    }

    override fun sendMessage(message: String) {
        val output = requireNotNull(socket).getOutputStream()
        val writer = PrintWriter(output)
        writer.print("$message\n")
        writer.flush()
    }

    override suspend fun awaitMessage(): String = suspendCancellableCoroutine { continuation ->
        val input =requireNotNull(socket).getInputStream()
        val scanner = Scanner(input)
        while (scanner.hasNext()) {
            continuation.resume(scanner.nextLine())
        }
    }
}

package n7.ad2.feature.games.xo.domain.internal.server.socket

import java.net.InetAddress
import java.net.Socket
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.feature.games.xo.domain.ClientHolder
import n7.ad2.feature.games.xo.domain.internal.server.ClientLog

internal class ClientHolderWithSocket : ClientHolder {

    companion object {
        object ClientSocketException : Exception()
    }

    var logger: (message: ClientLog) -> Unit = { }

    /**
     * @throws ClientSocketException - когда не удалось подключится
     */
    override suspend fun start(
        host: InetAddress?,
        port: Int,
    ): Socket = suspendCancellableCoroutine { continuation ->
        try {
            val socket = Socket(host, port)
            continuation.resume(socket)
            return@suspendCancellableCoroutine
        } catch (e: Exception) {
            continuation.resumeWithException(ClientSocketException)
        }
    }
}

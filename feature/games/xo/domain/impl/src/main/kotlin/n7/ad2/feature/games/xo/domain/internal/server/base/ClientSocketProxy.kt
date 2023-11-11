package n7.ad2.feature.games.xo.domain.internal.server.base

import java.net.InetAddress
import java.net.Socket
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class ClientSocketProxy {
    companion object {
        object ClientSocketException : Exception()
    }

    /**
     * @throws ClientSocketException - когда не удалось подключится
     */
    suspend fun start(
        host: InetAddress?,
        port: Int,
    ): Socket = suspendCancellableCoroutine { continuation ->
        try {
            val socket = Socket(host, port)
            continuation.resume(socket)
            return@suspendCancellableCoroutine
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
}

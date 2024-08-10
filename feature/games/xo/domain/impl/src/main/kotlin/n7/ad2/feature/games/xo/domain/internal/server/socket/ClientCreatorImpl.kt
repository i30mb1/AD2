@file:Suppress("JavaIoSerializableObjectMustHaveReadResolve")

package n7.ad2.feature.games.xo.domain.internal.server.socket

import java.net.InetAddress
import java.net.Socket
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.feature.games.xo.domain.ClientCreator

internal class ClientCreatorImpl : ClientCreator {

    companion object {
        data object ClientSocketException : Exception()
    }

    /**
     * @throws ClientSocketException - когда не удалось подключится
     */
    override suspend fun create(
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

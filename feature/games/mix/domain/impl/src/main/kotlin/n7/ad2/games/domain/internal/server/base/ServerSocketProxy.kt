package n7.ad2.games.domain.internal.server.base

import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.games.domain.internal.server.ServerLog

/**
 * Обертка над вызовами ServerSocket класса в suspend фукнции с логами
 */
class ServerSocketProxy {

    companion object {
        object ServerSocketException : Exception()
    }

    val logger: (message: ServerLog) -> Unit = { }

    /**
     * Запускает сервер на ip:порт
     * @param host - ip
     * @param ports - список портов, откроется на первом свободном порте
     *
     * @throws ServerSocketException - когда не удалось запустить сервер
     */
    suspend fun getServerSocket(
        host: InetAddress,
        ports: IntArray,
    ): ServerSocket = suspendCancellableCoroutine { continuation ->
        for (port in ports) {
            try {
                InetAddress.getByName(GameServer.host)
                val server = ServerSocket(port, 0, host)
                continuation.resume(server)
                return@suspendCancellableCoroutine
            } catch (e: Exception) {
                logger(ServerLog.PortBusy(port))
            }
        }
        logger(ServerLog.PortsBusy)
        continuation.resumeWithException(ServerSocketException)
    }

    suspend fun getClientSocket(
        serverSocket: ServerSocket,
    ): Socket = suspendCancellableCoroutine { continuation ->
        try {
            val socket = serverSocket.accept()
            continuation.resume(socket)
        } catch (error: Exception) {
            continuation.resumeWithException(error)
        }
    }
}

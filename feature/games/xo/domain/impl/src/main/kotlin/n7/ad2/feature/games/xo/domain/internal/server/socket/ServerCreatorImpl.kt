package n7.ad2.feature.games.xo.domain.internal.server.socket

import java.net.InetAddress
import java.net.ServerSocket
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.feature.games.xo.domain.ServerCreator
import n7.ad2.feature.games.xo.domain.internal.server.ServerLog
import n7.ad2.feature.games.xo.domain.model.SocketServerModel

/**
 * Обертка над вызовами ServerSocket класса в suspend фукнции с логами
 */
internal class ServerCreatorImpl : ServerCreator {

    companion object {
        object ServerSocketException : Exception()
    }

    val logger: (message: ServerLog) -> Unit = { }

    override suspend fun create(
        name: String,
        host: InetAddress,
        port: Int,
    ): SocketServerModel {
        val server = getServerSocket(name, host, intArrayOf(port))
        logger(ServerLog.ServerStarted(name, server.ip, server.port))
        return server
    }

    /**
     * Запускает сервер на ip:порт
     * @param host - ip
     * @param ports - список портов, откроется на первом свободном порте
     *
     * @throws ServerSocketException - не удалось запустить сервер
     */
    private suspend fun getServerSocket(
        name: String,
        host: InetAddress,
        ports: IntArray,
    ): SocketServerModel = suspendCancellableCoroutine { continuation ->
        for (port in ports) {
            try {
                val serverSocket = ServerSocket(port, 0, host)
                val server = SocketServerModel(
                    serverSocket = serverSocket,
                    name = name,
                    ip = serverSocket.inetAddress.hostAddress!!,
                    port = serverSocket.localPort,
                )
                continuation.resume(server)
                return@suspendCancellableCoroutine
            } catch (e: Exception) {
                logger(ServerLog.PortBusy(port))
            }
        }
        logger(ServerLog.StartFailed)
        continuation.resumeWithException(ServerSocketException)
    }
}

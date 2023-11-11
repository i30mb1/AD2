package n7.ad2.feature.games.xo.domain.internal.server.socket

import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.ServerHolder
import n7.ad2.feature.games.xo.domain.internal.server.ServerLog
import n7.ad2.feature.games.xo.domain.model.Server

/**
 * Обертка над вызовами ServerSocket класса в suspend фукнции с логами
 */
internal class ServerHolderWithSocket(
    private val registerServerInDNSUseCase: RegisterServiceInNetworkUseCase,
) : ServerHolder {

    companion object {
        object ServerSocketException : Exception()
    }

    sealed interface ServerWithSocketEvents {
        object Started : ServerWithSocketEvents
        object ClientConnected : ServerWithSocketEvents
    }

    val logger: (message: ServerLog) -> Unit = { }

    private val events = Channel<ServerWithSocketEvents>(Channel.BUFFERED)
    private var server: ServerSocket? = null

    override suspend fun start(
        host: InetAddress,
        name: String,
    ): ServerSocket {
        val server = getServerSocket(host, intArrayOf(0))
        registerServerInDNSUseCase(Server(name, server.inetAddress.hostAddress!!, server.localPort))
        this.server = server
        events.send(ServerWithSocketEvents.Started)
        return server
    }

    override suspend fun awaitClient(): Socket {
        val server = requireNotNull(server)
        val socket = getClientSocket(server)
        events.send(ServerWithSocketEvents.ClientConnected)
        return socket
    }

    /**
     * Запускает сервер на ip:порт
     * @param host - ip
     * @param ports - список портов, откроется на первом свободном порте
     *
     * @throws ServerSocketException - когда не удалось запустить сервер
     */
    private suspend fun getServerSocket(
        host: InetAddress,
        ports: IntArray,
    ): ServerSocket = suspendCancellableCoroutine { continuation ->
        for (port in ports) {
            try {
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

    private suspend fun getClientSocket(
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

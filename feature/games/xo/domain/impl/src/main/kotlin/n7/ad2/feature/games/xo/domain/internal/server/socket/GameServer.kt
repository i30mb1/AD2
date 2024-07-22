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
import n7.ad2.feature.games.xo.domain.model.SimpleServer

/**
 * Обертка над вызовами ServerSocket класса в suspend фукнции с логами
 */
internal class GameServer(
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
    private var server: Server? = null

    override suspend fun start(
        host: InetAddress,
        name: String,
    ): Server {
        val server = getServerSocket(name, host, intArrayOf(0))
        registerServerInDNSUseCase(server)
        this.server = server
        events.send(ServerWithSocketEvents.Started)
        return server
    }

    override suspend fun awaitClient(): Socket {
        val server = requireNotNull(server) { "server should not be null" }
        val socket = getClientSocket(server)
        events.send(ServerWithSocketEvents.ClientConnected)
        return socket
    }

    override suspend fun close() {
        server?.serverSocket?.close()
    }

    /**
     * Запускает сервер на ip:порт
     * @param host - ip
     * @param ports - список портов, откроется на первом свободном порте
     *
     * @throws ServerSocketException - когда не удалось запустить сервер
     */
    private suspend fun getServerSocket(
        name: String,
        host: InetAddress,
        ports: IntArray,
    ): Server = suspendCancellableCoroutine { continuation ->
        for (port in ports) {
            try {
                val serverSocket = ServerSocket(port, 0, host)
                val server = SimpleServer(
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

    private suspend fun getClientSocket(
        server: Server,
    ): Socket = suspendCancellableCoroutine { continuation ->
        try {
            val socket = server.serverSocket.accept()
            continuation.resume(socket)
        } catch (error: Exception) {
            continuation.resumeWithException(error)
        }
    }
}

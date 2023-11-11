package n7.ad2.xo.internal.game

import java.net.InetAddress
import java.net.NetworkInterface
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.ClientHolder
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.ServerHolder
import n7.ad2.feature.games.xo.domain.SocketHolder
import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.xo.internal.mapper.ServerToServerUIMapper

internal class GameLogic @Inject constructor(
    private val serverHolder: ServerHolder,
    private val clientHolder: ClientHolder,
    private val discoverServicesInNetworkUseCase: DiscoverServicesInNetworkUseCase,
    private val dispatchers: DispatchersProvider,
    private val socketHolder: SocketHolder,
) {

    private val scope = CoroutineScope(Job() + dispatchers.IO)
    private val _state: MutableStateFlow<GameState> = MutableStateFlow(GameState.init())
    val state: MutableStateFlow<GameState> = _state

    fun init(): Flow<List<Server>> {
        return discoverServicesInNetworkUseCase()
            .onStart {
                _state.setDeviceIP(getIPAddress())
            }
            .onEach { servers: List<Server> ->
                _state.setServers(servers.map(ServerToServerUIMapper))
            }
            .flowOn(dispatchers.IO)
    }

    suspend fun startServer(name: String) = withContext(dispatchers.IO) {
        val ip = InetAddress.getByName(state.value.deviceIP)
        serverHolder.start(ip, name)
        socketHolder.socket = serverHolder.awaitClient()
        _state.addLog("Client Connected")
        collectMessages()
    }

    suspend fun sendMessage(message: String) = withContext(dispatchers.IO) {
        socketHolder.sendMessage(message)
    }

    suspend fun connectToServer(ip: InetAddress, port: Int) = withContext(dispatchers.IO) {
        socketHolder.socket = clientHolder.start(ip, port)
        _state.addLog("Connected to Server")
        collectMessages()
    }

    private fun CoroutineScope.collectMessages() = launch {
        while (true) {
            val message = socketHolder.awaitMessage()
            _state.addLog("client: $message")
        }
    }

    private fun getIPAddress(): String {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val adress = inetAddresses.nextElement()
                if (!adress.isLoopbackAddress && !adress.isLinkLocalAddress && adress.isSiteLocalAddress) {
                    return adress.hostAddress!!
                }
            }
        }
        error("Could find ip adress")
    }
}

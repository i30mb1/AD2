package n7.ad2.xo.internal.game

import java.net.InetAddress
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.ClientHolder
import n7.ad2.feature.games.xo.domain.ConnectToWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.DiscoverServicesInWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.GetDeviceNameUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.ServerHolder
import n7.ad2.feature.games.xo.domain.SocketHolder
import n7.ad2.feature.games.xo.domain.model.NetworkState
import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.xo.internal.mapper.NetworkToIPMapper

internal class GameLogic @Inject constructor(
    private val serverHolder: ServerHolder,
    private val clientHolder: ClientHolder,
    private val discoverServicesInNetworkUseCase: DiscoverServicesInNetworkUseCase,
    private val discoverServicesInWifiDirectUseCase: DiscoverServicesInWifiDirectUseCase,
    private val connectToWifiDirectUseCase: ConnectToWifiDirectUseCase,
    private val getNetworkStateUseCase: GetNetworkStateUseCase,
    private val getDeviceNameUseCase: GetDeviceNameUseCase,
    private val dispatchers: DispatchersProvider,
    private val socketHolder: SocketHolder,
) {

    private val _state: MutableStateFlow<GameState> = MutableStateFlow(GameState.init())
    val state: StateFlow<GameState> = _state.asStateFlow()

    fun init(scope: CoroutineScope) {
        merge(
            combine(
                discoverServicesInNetworkUseCase(),
                discoverServicesInWifiDirectUseCase(),
            ) { servers: List<Server>, serversDirect: List<Server> -> servers + serversDirect },
            getNetworkStateUseCase(),
        )
            .onStart { _state.setDeviceName(getDeviceNameUseCase()) }
            .transform<Any, Unit> { value ->
                when (value) {
                    is NetworkState -> _state.setDeviceIP(NetworkToIPMapper(value))
                    is List<*> -> _state.setServers(value as List<Server>)
                }
            }
            .launchIn(scope)
    }

    suspend fun startServer(name: String) = withContext(dispatchers.IO) {
        val ip = InetAddress.getByName(_state.value.deviceIP)
        serverHolder.start(ip, name)
        socketHolder.socket = serverHolder.awaitClient()
        _state.addLog("Client Connected")
        collectMessages()
    }

    suspend fun sendMessage(message: String) = withContext(dispatchers.IO) {
        socketHolder.sendMessage(message)
    }

    suspend fun connectToServer(server: Server) = withContext(dispatchers.IO) {
        socketHolder.socket = clientHolder.start(InetAddress.getByName(server.serverIP), server.port)
        _state.addLog("Connected to Server")
        collectMessages()
    }

    suspend fun connectToWifiDirect(serverIP: String) = withContext(dispatchers.IO) {
        connectToWifiDirectUseCase(serverIP)
//        serverHolder.start(InetAddress.getByName(serverIP), "H1")
//        socketHolder.socket = serverHolder.awaitClient()
//        _state.addLog("Client Connected")
//        collectMessages()
    }

    private fun CoroutineScope.collectMessages() = launch(Job()) {
        while (socketHolder.socket?.isConnected == true) {
            val message = socketHolder.awaitMessage()
            _state.addLog("client: $message")
        }
    }
}

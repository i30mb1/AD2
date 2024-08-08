package n7.ad2.xo.internal.game

import java.net.InetAddress
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.ClientHolder
import n7.ad2.feature.games.xo.domain.ConnectToWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.DiscoverServicesInWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.GetDeviceNameUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.ServerHolder
import n7.ad2.feature.games.xo.domain.SocketMessanger
import n7.ad2.feature.games.xo.domain.internal.server.socket.GameSocketMessenger
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
    private val registerServerInDNSUseCase: RegisterServiceInNetworkUseCase,
    private val logger: Logger,
) {

    private val _state: MutableStateFlow<GameState> = MutableStateFlow(GameState.init())
    val state: StateFlow<GameState> = _state.asStateFlow()
    private val socketMessanger: SocketMessanger = GameSocketMessenger()

    suspend fun init() = coroutineScope {
        merge(
            combine(
                discoverServicesInNetworkUseCase(),
                flowOf(emptyList())
//                discoverServicesInWifiDirectUseCase(),
            ) { servers: List<Server>, serversDirect: List<Server> ->
                servers + serversDirect
            },
            getNetworkStateUseCase(),
        )
            .onStart { _state.setDeviceName(getDeviceNameUseCase()) }
            .transform<Any, Unit> { value ->
                when (value) {
                    is NetworkState -> _state.setDeviceIP(NetworkToIPMapper(value))
                    is List<*> -> _state.setServers(value as List<Server>)
                }
            }
            .flowOn(dispatchers.IO)
            .launchIn(this)
    }

    /**
     * функция никогда не заверишится
     */
    suspend fun startServer(name: String) = withContext(dispatchers.IO) {
        val ip = InetAddress.getByName(_state.value.deviceIP)
        var server = serverHolder.start(ip, name)
        logger.log("Start Server ${server.name}")
        logger.log("Await Client on ${server.ip}:${server.port}")

        server = registerServerInDNSUseCase.register(server)

        _state.setServerState(ServerState.Connecting(server))
        val socket = serverHolder.awaitClient()
        _state.setServerState(ServerState.Connected(server, true))
        socketMessanger.init(socket)
        logger.log("Client Connected")

        collectMessages()
    }

    suspend fun sendMessage(message: String) = withContext(dispatchers.IO) {
        _state.addServerMessage(message)
        socketMessanger.sendMessage(message)
    }

    /**
     * функция никогда не заверишится
     */
    suspend fun connectToServer(server: Server) = withContext(dispatchers.IO) {
        val socket = clientHolder.start(InetAddress.getByName(server.ip), server.port)
        socketMessanger.init(socket)
        _state.setServerState(ServerState.Connected(server, false))
        logger.log("Connected to Server")
        collectMessages()
    }

    suspend fun connectToWifiDirect(serverIP: String) = withContext(dispatchers.IO) {
//        connectToWifiDirectUseCase(serverIP)
//        serverHolder.start(InetAddress.getByName(serverIP), "H1")
//        socketHolder.socket = serverHolder.awaitClient()
//        _state.addLog("Client Connected")
//        collectMessages()
    }

    fun onClear() {
        registerServerInDNSUseCase.unregister()
    }

    /**
     * Запускаем в отдельной Job чтобы закончить suspend функцию которая ее вызывает
     */
    private fun CoroutineScope.collectMessages() = launch {
        while (socketMessanger.isConnected()) {
            val message = socketMessanger.awaitMessage()
            if (message != null) _state.addClientMessage("$message")
        }
        serverHolder.close()
        _state.setServerState(ServerState.Disconected)
        logger.log("Disconnected from Server")
    }
}

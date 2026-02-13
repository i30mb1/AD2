package n7.ad2.xo.internal.game

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.ConnectToWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.DiscoverServicesInWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.GetDeviceNameUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.internal.server.controller.ClientController
import n7.ad2.feature.games.xo.domain.internal.server.controller.HttpClientController
import n7.ad2.feature.games.xo.domain.internal.server.controller.HttpServerController
import n7.ad2.feature.games.xo.domain.internal.server.controller.ServerController
import n7.ad2.feature.games.xo.domain.internal.server.controller.SocketClientController
import n7.ad2.feature.games.xo.domain.internal.server.controller.SocketServerController
import n7.ad2.feature.games.xo.domain.internal.server.controller.WebsocketClientController
import n7.ad2.feature.games.xo.domain.internal.server.controller.WebsocketServerController
import n7.ad2.feature.games.xo.domain.internal.server.data.ClientStatus
import n7.ad2.feature.games.xo.domain.internal.server.data.ServerStatus
import n7.ad2.feature.games.xo.domain.model.NetworkState
import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.xo.internal.mapper.NetworkToIPMapper
import n7.ad2.xo.internal.model.SocketType
import java.net.InetAddress
import javax.inject.Inject

internal class GameLogic @Inject constructor(
    private val discoverServicesInNetworkUseCase: DiscoverServicesInNetworkUseCase,
    private val discoverServicesInWifiDirectUseCase: DiscoverServicesInWifiDirectUseCase,
    private val connectToWifiDirectUseCase: ConnectToWifiDirectUseCase,
    private val getNetworkStateUseCase: GetNetworkStateUseCase,
    private val getDeviceNameUseCase: GetDeviceNameUseCase,
    private val dispatchers: DispatchersProvider,
    private val registerServerInDNSUseCase: RegisterServiceInNetworkUseCase,
) {

    private var socketServerController: ServerController = SocketServerController()
    private var socketClientController: ClientController = SocketClientController()
    private val _state: MutableStateFlow<GameState> = MutableStateFlow(GameState.init())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var serverJob: Job? = null
    private var clientJob: Job? = null

    suspend fun init() = coroutineScope {
        merge(
            combine(
                discoverServicesInNetworkUseCase(),
                flowOf(emptyList()),
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

        mainScope = this
        subscribeToControllers(this)
    }

    suspend fun startServer(name: String) = withContext(dispatchers.IO) {
        val ip = InetAddress.getByName(_state.value.deviceIP)
        socketServerController.start(name, ip)
    }

    suspend fun sendMessage(text: String) = withContext(dispatchers.IO) {
        when (val status = state.value.gameStatus) {
            is GameStatus.Started -> {
                if (status.isHost) {
                    socketServerController.send(text)
                } else {
                    socketClientController.send(text)
                }
            }

            else -> Unit
        }
    }

    suspend fun connectToServer(server: Server) = withContext(dispatchers.IO) {
        val ip = InetAddress.getByName(server.ip)
        socketClientController.connect(server.name, ip, server.port)
    }

    suspend fun connectToWifiDirect(serverIP: String) = withContext(dispatchers.IO) {
//        connectToWifiDirectUseCase(serverIP)
//        serverHolder.start(InetAddress.getByName(serverIP), "H1")
//        socketHolder.socket = serverHolder.awaitClient()
//        _state.addLog("Client Connected")
//        collectMessages()
    }

    fun selectSocketType(socketType: SocketType) {
        _state.setSocketType(socketType)
        updateControllersAndResubscribe(socketType)
    }

    private var mainScope: kotlinx.coroutines.CoroutineScope? = null

    private fun subscribeToControllers(scope: kotlinx.coroutines.CoroutineScope) {
        // Отменяем предыдущие подписки
        serverJob?.cancel()
        clientJob?.cancel()

        // Подписываемся на server controller
        serverJob = socketServerController.state
            .onEach { state ->
                when (val status = state.status) {
                    is ServerStatus.Waiting -> registerServerInDNSUseCase.register(status.server)
                    else -> registerServerInDNSUseCase.unregister()
                }
                _state.update {
                    it.copy(
                        messages = state.messages,
                        gameStatus = when (val status = state.status) {
                            is ServerStatus.Connected -> GameStatus.Started(true, status.server)
                            ServerStatus.Closed -> GameStatus.Idle
                            is ServerStatus.Waiting -> GameStatus.Waiting
                        },
                    )
                }
            }
            .onCompletion {
                socketServerController.stop()
                registerServerInDNSUseCase.unregister()
            }
            .launchIn(scope)

        // Подписываемся на client controller
        clientJob = socketClientController.state
            .onEach { state ->
                _state.update {
                    it.copy(
                        gameStatus = when (val status = state.status) {
                            is ClientStatus.Connected -> GameStatus.Started(false, status.server)
                            ClientStatus.Disconnected -> GameStatus.Idle
                        },
                        messages = state.messages,
                    )
                }
            }
            .onCompletion {
                socketClientController.disconnect()
            }
            .launchIn(scope)
    }

    private fun updateControllersAndResubscribe(socketType: SocketType) {
        // Останавливаем текущие контроллеры
        socketServerController.stop()
        socketClientController.disconnect()

        // Создаем новые контроллеры в зависимости от типа
        when (socketType) {
            SocketType.RAW -> {
                socketServerController = SocketServerController()
                socketClientController = SocketClientController()
            }

            SocketType.HTTP -> {
                socketServerController = HttpServerController()
                socketClientController = HttpClientController()
            }

            SocketType.WEBSOCKET -> {
                socketServerController = WebsocketServerController()
                socketClientController = WebsocketClientController()
            }
        }

        // Переподписываемся на новые контроллеры
        mainScope?.let { subscribeToControllers(it) }
    }
}

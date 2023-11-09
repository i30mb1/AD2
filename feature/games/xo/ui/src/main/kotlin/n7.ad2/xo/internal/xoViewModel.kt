package n7.ad2.xo.internal

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.net.InetAddress
import java.net.NetworkInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.ClientHolder
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.ServerHolder
import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.mapper.ServerToServerUIMapper
import n7.ad2.xo.internal.model.XoState
import n7.ad2.xo.internal.model.addLog
import n7.ad2.xo.internal.model.disableStart
import n7.ad2.xo.internal.model.setDeviceIP
import n7.ad2.xo.internal.model.setServers
import n7.ad2.xo.internal.model.startGame


internal class XoViewModel @AssistedInject constructor(
    private val context: Application,
    private val dispatchers: DispatchersProvider,
    private val serverHolder: ServerHolder,
    private val clientHolder: ClientHolder,
    private val discoverServer: DiscoverServicesInNetworkUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<XoState> = MutableStateFlow(XoState.init())
    val state: StateFlow<XoState> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(): XoViewModel
    }

    init {
        viewModelScope.launch(dispatchers.IO) {
            _state.setDeviceIP(getIPAddress())
        }
        discoverServer()
            .onEach { servers: List<Server> ->
                _state.setServers(
                    servers.map(ServerToServerUIMapper)
                )
            }
            .flowOn(dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun connectToServer(server: ServerUI) = viewModelScope.launch(dispatchers.IO) {
        clientHolder.start(InetAddress.getByName(server.serverIP), server.port.toInt())
        _state.addLog("Connected to Server")
        _state.startGame()
        while (true) {
            val message = clientHolder.awaitMessage()
            _state.addLog("client: $message")
        }
    }

    fun runServer(name: String) = viewModelScope.launch(dispatchers.IO) {
        val ip = InetAddress.getByName(state.value.deviceIP)
        serverHolder.start(ip, name)
        _state.disableStart()
        serverHolder.awaitClient()
        _state.addLog("Client Connected")
        _state.startGame()
        while (true) {
            val message = serverHolder.awaitMessage()
            _state.addLog("server: $message")
        }
    }

    fun sendPong() = viewModelScope.launch(dispatchers.IO) {
        clientHolder.sendMessage("pong")
    }

    fun sendPing() = viewModelScope.launch(dispatchers.IO) {
        serverHolder.sendMessage("ping")
    }
}

internal fun getIPAddress(): String {
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


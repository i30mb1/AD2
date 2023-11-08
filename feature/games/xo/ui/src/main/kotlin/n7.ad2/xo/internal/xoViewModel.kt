package n7.ad2.xo.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.net.InetAddress
import java.net.NetworkInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.Client
import n7.ad2.feature.games.xo.domain.Server
import n7.ad2.xo.internal.model.ServerUI
import n7.ad2.xo.internal.model.XoState
import n7.ad2.xo.internal.model.addLog
import n7.ad2.xo.internal.model.disableStart
import n7.ad2.xo.internal.model.startGame


internal class XoViewModel @AssistedInject constructor(
    private val dispatchers: DispatchersProvider,
    private val server: Server,
    private val client: Client,
) : ViewModel() {

    private val _state: MutableStateFlow<XoState> = MutableStateFlow(XoState.init())
    val state: StateFlow<XoState> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(): XoViewModel
    }

    init {
        viewModelScope.launch(dispatchers.IO) {
            _state.update { state ->
                state.copy(
                    deviceIP = getIPAddress(),
                    servers = getAllIpAdress(),
                )
            }
        }
    }

    fun connectToServer(ip: String) = viewModelScope.launch(dispatchers.IO) {
        client.start(InetAddress.getByName(ip), 8080)
        _state.addLog("Connected to Server")
        _state.startGame()
        while (true) {
            val message = client.awaitMessage()
            _state.addLog("client: $message")
        }
    }

    fun runServer() = viewModelScope.launch(dispatchers.IO) {
        val ip = InetAddress.getByName(state.value.deviceIP)
        server.start(ip, intArrayOf(8080))
        _state.disableStart()
        server.awaitClient()
        _state.addLog("Client Connected")
        _state.startGame()
        while (true) {
            val message = server.awaitMessage()
            _state.addLog("server: $message")
        }
    }

    fun sendPong() {
        client.sendMessage("pong")
    }

    fun sendPing() {
        server.sendMessage("ping")
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

internal fun getAllIpAdress(): List<ServerUI> {
    return buildList {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val adress = inetAddresses.nextElement()
                if (adress.isReachable(1)) {
                    val ip = adress.hostAddress!!
                    add(ServerUI(ip))
                }
            }
        }
    }
}

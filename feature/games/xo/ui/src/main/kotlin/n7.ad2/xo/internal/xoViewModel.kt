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
import kotlinx.coroutines.launch
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.Client
import n7.ad2.feature.games.xo.domain.Server

internal class XoViewModel @AssistedInject constructor(
    private val dispatchers: DispatchersProvider,
    private val server: Server,
    private val client: Client,
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(): XoViewModel
    }

    init {
        viewModelScope.launch(dispatchers.IO) {
            _state.value = State.Data(getIPAddress())
        }
    }

    fun runClient() = viewModelScope.launch(dispatchers.IO) {
//        client.start(ip, 8080)
//        text = "Connected to Server\n"
        while (true) {
            val message = client.awaitMessage()
//            text += "$message\n"
        }
    }

    fun runServer() = viewModelScope.launch(dispatchers.IO) {
        val ip = InetAddress.getByName((state.value as State.Data).ip)
        server.start(ip, intArrayOf(8080))
        server.awaitClient()
//        text = "Client Connected\n"
        while (true) {
            val message = server.awaitMessage()
//            text += "$message\n"
        }
    }

    fun sendPong() {
        client.sendMessage("pong")
    }

    fun sendPing() {
        server.sendMessage("ping")
    }

    sealed interface State {
        object Loading : State
        data class Data(val ip: String) : State
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

package n7.ad2.xo.internal

import android.app.Application
import android.net.nsd.NsdManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.net.InetAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import n7.ad2.app.logger.Logger
import n7.ad2.app.logger.model.AppLog
import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.game.GameLogic
import n7.ad2.xo.internal.game.GameState
import n7.ad2.xo.internal.mapper.ServerToServerUIMapper

internal class XoViewModel @AssistedInject constructor(
    private val gameLogic: GameLogic,
    private val logger: Logger,
    private val application: Application,
) : ViewModel() {

    private val _state: MutableStateFlow<XoUIState> = MutableStateFlow(XoUIState.init())
    val state: StateFlow<XoUIState> = combine(
        _state,
        gameLogic.state,
        logger.getLogFlow().scan(emptyList()) { accumulator, value -> accumulator + value },
    ) { xoUIState: XoUIState, gameState: GameState, logs: List<AppLog> ->
        xoUIState.copy(
            deviceIP = gameState.deviceIP,
            servers = gameState.servers.map(ServerToServerUIMapper),
            messages = gameState.logs,
            deviceName = gameState.deviceName,
            logs = logs,
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, XoUIState.init())

    @AssistedFactory
    interface Factory {
        fun create(): XoViewModel
    }

    init {
        gameLogic.init().launchIn(viewModelScope)
    }

    fun connectToServer(serverUI: ServerUI) = viewModelScope.launch {
        val server: Server = gameLogic.state.value.servers.find { server -> server.name == serverUI.name }!!
        if (server.isWifiDirect) {
            gameLogic.connectToWifiDirect(server.serverIP)
        } else {
            gameLogic.connectToServer(server)
        }
        _state.startGame()
    }

    fun runServer(name: String) = viewModelScope.launch {
        _state.disableStart()
        gameLogic.startServer(name)
        _state.startGame()
    }

    fun sendPong() = viewModelScope.launch {
        gameLogic.sendMessage("pong")
    }

    fun sendPing() = viewModelScope.launch {
        gameLogic.sendMessage("ping")
    }
}

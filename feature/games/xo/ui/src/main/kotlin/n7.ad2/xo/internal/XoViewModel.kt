package n7.ad2.xo.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.net.InetAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import n7.ad2.app.logger.Logger
import n7.ad2.app.logger.model.AppLog
import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.game.GameLogic
import n7.ad2.xo.internal.game.GameState

internal class XoViewModel @AssistedInject constructor(
    private val gameLogic: GameLogic,
    private val logger: Logger,
) : ViewModel() {

    private val _state: MutableStateFlow<XoState> = MutableStateFlow(XoState.init())
    val state: StateFlow<XoState> = combine(
        _state,
        gameLogic.state,
        logger.getLogFlow().scan(emptyList()) { accumulator, value -> accumulator + value },
    ) { xoState: XoState, gameState: GameState, logs: List<AppLog> ->
        xoState.copy(
            deviceIP = gameState.deviceIP,
            servers = gameState.servers,
            messages = gameState.logs,
            deviceName = gameState.deviceName,
            logs = logs,
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, XoState.init())

    @AssistedFactory
    interface Factory {
        fun create(): XoViewModel
    }

    init {
        gameLogic.init().launchIn(viewModelScope)
    }

    fun connectToServer(server: ServerUI) = viewModelScope.launch {
        gameLogic.connectToServer(InetAddress.getByName(server.serverIP), server.port.toInt())
        //TODO() хуле не вызывается
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

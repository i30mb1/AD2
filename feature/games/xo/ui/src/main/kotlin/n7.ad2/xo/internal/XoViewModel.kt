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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.game.GameLogic
import n7.ad2.xo.internal.game.GameState

internal class XoViewModel @AssistedInject constructor(
    private val gameLogic: GameLogic,
) : ViewModel() {

    private val _state: MutableStateFlow<XoState> = MutableStateFlow(XoState.init())
    val state: StateFlow<XoState> = combine(_state, gameLogic.state) { xoState: XoState, gameState: GameState ->
        xoState.copy(
            deviceIP = gameState.deviceIP,
            servers = gameState.servers,
            logs = gameState.logs,
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

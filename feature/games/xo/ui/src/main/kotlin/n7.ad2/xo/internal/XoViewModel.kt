package n7.ad2.xo.internal

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.net.InetAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.game.GameLogic

internal class XoViewModel @AssistedInject constructor(
    private val gameLogic: GameLogic,
) : ViewModel() {

    val state: MutableStateFlow<XoState> = gameLogic.state.map {
        XoState.init()
    }.laun(initial = XoState.init())

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

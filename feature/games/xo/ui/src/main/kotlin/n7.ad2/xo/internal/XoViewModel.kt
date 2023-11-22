package n7.ad2.xo.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
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
    logger: Logger,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): XoViewModel
    }

    private val _state: MutableStateFlow<XoUIState> = MutableStateFlow(XoUIState.init())
    val state: StateFlow<XoUIState> = _state.asStateFlow()

    init {
        merge(gameLogic.state, logger.getLogsFlow())
            .onStart { gameLogic.init(viewModelScope) }
            .transform<Any, Unit> { value: Any ->
                when (value) {
                    is GameState -> {
                        _state.update {
                            it.copy(
                                deviceIP = value.deviceIP,
                                servers = value.servers.map(ServerToServerUIMapper),
                                messages = value.logs,
                                deviceName = value.deviceName,
                            )
                        }
                    }

                    is List<*> -> {
                        _state.updateLogs(value as List<AppLog>)
                    }
                }
            }
            .launchIn(viewModelScope)
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

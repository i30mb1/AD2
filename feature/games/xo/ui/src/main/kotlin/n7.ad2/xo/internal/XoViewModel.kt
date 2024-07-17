package n7.ad2.xo.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.app.logger.Logger
import n7.ad2.app.logger.model.AppLog
import n7.ad2.feature.games.xo.domain.model.SimpleServer
import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.game.GameLogic
import n7.ad2.xo.internal.game.GameState
import n7.ad2.xo.internal.mapper.ServerToServerUIMapper

internal class XoViewModel @AssistedInject constructor(
    private val gameLogic: GameLogic,
    private val logger: Logger,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): XoViewModel
    }

    private val _state: MutableStateFlow<XoUIState> = MutableStateFlow(XoUIState.init())
    val state: StateFlow<XoUIState> = _state.asStateFlow()

    init {
        merge(
            logger.getLogFlow(),
            ticker(2.seconds.inWholeMilliseconds).consumeAsFlow()
        )
            .transform<Any, Unit> { value: Any ->
                when (value) {
                    is AppLog -> _state.updateLogs(_state.value.logs + value)
                    else -> _state.updateLogs(_state.value.logs.drop(1))
                }
            }
            .launchIn(viewModelScope)

        gameLogic.state
            .onStart { gameLogic.init(viewModelScope) }
            .onEach { state: GameState ->
                _state.update {
                    it.copy(
                        deviceIP = state.deviceIP,
                        servers = state.servers.map(ServerToServerUIMapper),
                        messages = state.messages,
                        deviceName = state.deviceName,
                        isGameStarted = state.isConnected,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun connectToServer(serverUI: ServerUI) = flowOf(serverUI)
        .onEach {
//        val server: Server = gameLogic.state.value.servers.find { server -> server.name == serverUI.name }!!
//        if (server.isWifiDirect) {
//            gameLogic.connectToWifiDirect(server.serverIP)
//        } else {
            val port = serverUI.port.toIntOrNull() ?: error("port is empty")
            gameLogic.connectToServer(SimpleServer(serverUI.name, serverUI.serverIP, port))
//        }
            _state.startGame()
        }
        .catch { error -> logger.log("Connect error $error") }
        .launchIn(viewModelScope)

    fun runServer(name: String) = viewModelScope.launch {
        _state.isButtonStartVisible(false)
        gameLogic.startServer(name)
        _state.isButtonStartVisible(true)
    }

    fun sendPong() = viewModelScope.launch {
        gameLogic.sendMessage("pong")
    }

    fun sendPing() = viewModelScope.launch {
        gameLogic.sendMessage("ping")
    }
}

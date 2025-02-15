package n7.ad2.xo.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.app.logger.Logger
import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.game.GameLogic
import n7.ad2.xo.internal.game.GameState
import n7.ad2.xo.internal.game.GameStatus
import n7.ad2.xo.internal.mapper.ServerToServerUIMapper

internal class XoViewModel @AssistedInject constructor(
    private val gameLogic: GameLogic,
    private val logger: Logger,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): XoViewModel
    }

    private val _state: MutableStateFlow<XoUIState> = MutableStateFlow(XoUIState())
    val state: StateFlow<XoUIState> = _state.asStateFlow()

    init {
        viewModelScope.launch { gameLogic.init() }
        logger.getLogFlow()
            .flatMapMerge { log ->
                _state.updateLogs(_state.value.logs + log)
                flow<Unit> {
                    delay(20.seconds)
                    _state.updateLogs(_state.value.logs.drop(1))
                }
            }
            .launchIn(viewModelScope)

        gameLogic.state
            .onEach { state: GameState ->
                _state.update {
                    it.copy(
                        deviceIP = state.deviceIP,
                        servers = state.servers.map { server ->
                            ServerToServerUIMapper(server, state.deviceIP)
                        },
                        messages = state.messages,
                        deviceName = state.deviceName,
                        isGameStarted = state.gameStatus is GameStatus.Started,
                        isButtonStartEnabled = state.gameStatus is GameStatus.Idle,
                        server = (state.gameStatus as? GameStatus.Started)?.server
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun connectToServer(serverUI: ServerUI) = flowOf(serverUI)
        .onEach {
            val server: Server = gameLogic.state.value.servers.find { server -> server.name == serverUI.name }!!
//        if (server.isWifiDirect) {
//            gameLogic.connectToWifiDirect(server.serverIP)
//        } else {
            gameLogic.connectToServer(server)
//        }
        }
        .catch { error -> logger.log("Connect error $error") }
        .flowOn(Dispatchers.IO)
        .launchIn(viewModelScope)

    fun runServer(name: String) = viewModelScope.launch {
        gameLogic.startServer(name)
    }

    fun sendMessage(message: String) = viewModelScope.launch {
        gameLogic.sendMessage(message)
    }
}

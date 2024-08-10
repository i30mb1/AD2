package n7.ad2.xo.internal.game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import n7.ad2.feature.games.xo.domain.internal.server2.data.Message
import n7.ad2.feature.games.xo.domain.model.Server

internal data class GameState(
    val deviceIP: String,
    val deviceName: String,
    val port: String,
    val servers: List<Server> = emptyList(),
    val gameStatus: GameStatus = GameStatus.Waiting,
    val messages: List<Message> = emptyList(),
) {

    companion object {
        fun init() = GameState(
            "",
            "",
            "",
        )
    }
}

sealed interface GameStatus {
    data class Started(val isHost: Boolean) : GameStatus
    data object Waiting : GameStatus
    data object Idle : GameStatus
}

internal fun MutableStateFlow<GameState>.setDeviceIP(ip: String) = update { it.copy(deviceIP = ip) }

internal fun MutableStateFlow<GameState>.setDeviceName(name: String) = update { it.copy(deviceName = name) }

internal fun MutableStateFlow<GameState>.setServers(servers: List<Server>) = update { it.copy(servers = servers) }

internal fun MutableStateFlow<GameState>.addClientMessage(message: String) = update { it.copy(messages = it.messages + Message.Me(message)) }

internal fun MutableStateFlow<GameState>.addServerMessage(message: String) = update { it.copy(messages = it.messages + Message.Other(message)) }

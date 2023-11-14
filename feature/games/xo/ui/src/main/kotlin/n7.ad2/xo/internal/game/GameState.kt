package n7.ad2.xo.internal.game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import n7.ad2.xo.internal.compose.model.ServerUI

internal data class GameState(
    val deviceIP: String,
    val deviceName: String,
    val port: String,
    val servers: List<ServerUI>,
    val logs: List<String> = emptyList(),
) {

    companion object {
        fun init() = GameState(
            "",
            "",
            "",
            emptyList(),
        )
    }
}

internal fun MutableStateFlow<GameState>.setDeviceIP(ip: String) = update { it.copy(deviceIP = ip) }

internal fun MutableStateFlow<GameState>.setDeviceName(name: String) = update { it.copy(deviceName = name) }

internal fun MutableStateFlow<GameState>.setServers(servers: List<ServerUI>) = update { it.copy(servers = servers) }

internal fun MutableStateFlow<GameState>.addLog(log: String) = update { it.copy(logs = it.logs + log) }

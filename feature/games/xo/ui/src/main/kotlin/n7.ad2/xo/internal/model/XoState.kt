package n7.ad2.xo.internal.model

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.xo.internal.compose.model.ServerUI

@Immutable
internal data class XoState(
    val deviceIP: String,
    val port: String,
    val servers: List<ServerUI>,
    val isGameStarted: Boolean = false,
    val isStartEnabled: Boolean = true,
    val logs: List<String> = emptyList(),
) {

    companion object {
        fun init() = XoState(
            "",
            "",
            emptyList(),
        )
    }
}

internal fun MutableStateFlow<XoState>.startGame() = update { it.copy(isGameStarted = true) }

internal fun MutableStateFlow<XoState>.setDeviceIP(ip: String) = update { it.copy(deviceIP = ip) }

internal fun MutableStateFlow<XoState>.setServers(servers: List<ServerUI>) = update { it.copy(servers = servers) }

internal fun MutableStateFlow<XoState>.disableStart() = update { it.copy(isStartEnabled = false) }

internal fun MutableStateFlow<XoState>.addLog(log: String) = update { it.copy(logs = it.logs + log) }

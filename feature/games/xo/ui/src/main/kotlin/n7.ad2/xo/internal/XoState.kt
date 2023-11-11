package n7.ad2.xo.internal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import n7.ad2.xo.internal.compose.model.ServerUI

internal data class XoState(
    val deviceIP: String,
    val servers: List<ServerUI>,
    val isGameStarted: Boolean,
    val isStartEnabled: Boolean,
    val logs: List<String>,
) {

    companion object {
        fun init() = XoState(
            deviceIP = "",
            servers = emptyList(),
            isGameStarted = false,
            isStartEnabled = true,
            logs = emptyList()
        )
    }
}

internal fun MutableStateFlow<XoState>.disableStart() = update { it.copy(isStartEnabled = false) }

internal fun MutableStateFlow<XoState>.startGame() = update { it.copy(isGameStarted = true) }

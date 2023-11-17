package n7.ad2.xo.internal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import n7.ad2.app.logger.model.AppLog
import n7.ad2.xo.internal.compose.model.ServerUI

internal data class XoState(
    val deviceIP: String,
    val deviceName: String,
    val servers: List<ServerUI>,
    val isGameStarted: Boolean,
    val isStartEnabled: Boolean,
    val messages: List<String>,
    val logs: List<AppLog>,
) {

    companion object {
        fun init() = XoState(
            deviceIP = "",
            deviceName = "",
            servers = emptyList(),
            isGameStarted = false,
            isStartEnabled = true,
            messages = emptyList(),
            logs = emptyList(),
        )
    }
}

internal fun MutableStateFlow<XoState>.disableStart() = update {
    it.copy(isStartEnabled = false)
}

internal fun MutableStateFlow<XoState>.startGame() = update {
    it.copy(isGameStarted = true)
}

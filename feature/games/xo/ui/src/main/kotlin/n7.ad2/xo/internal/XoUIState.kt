package n7.ad2.xo.internal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import n7.ad2.app.logger.model.AppLog
import n7.ad2.xo.internal.compose.model.ServerUI

internal data class XoUIState(
    val deviceIP: String,
    val deviceName: String,
    val servers: List<ServerUI>,
    val isGameStarted: Boolean,
    val isStartEnabled: Boolean,
    val messages: List<String>,
    val logs: List<AppLog>,
) {

    companion object {
        fun init() = XoUIState(
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

internal fun MutableStateFlow<XoUIState>.disableStart(): XoUIState = updateAndGet {
    it.copy(isStartEnabled = false)
}

internal fun MutableStateFlow<XoUIState>.startGame() = update {
    it.copy(isGameStarted = true)
}

internal fun MutableStateFlow<XoUIState>.updateLogs(logs: List<AppLog>) = update {
    it.copy(logs = logs)
}

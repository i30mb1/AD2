package n7.ad2.xo.internal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import n7.ad2.app.logger.model.AppLog
import n7.ad2.feature.games.xo.domain.internal.server.data.Message
import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.model.SocketType

internal data class XoUIState(
    val deviceIP: String = "",
    val deviceName: String = "",
    val servers: List<ServerUI> = emptyList(),
    val isGameStarted: Boolean = false,
    val isButtonStartEnabled: Boolean = true,
    val messages: List<Message> = emptyList(),
    val logs: List<AppLog> = emptyList(),
    val server: Server? = null,
    val selectedSocketType: SocketType = SocketType.RAW,
)

internal fun MutableStateFlow<XoUIState>.updateLogs(logs: List<AppLog>) = update {
    it.copy(logs = logs)
}

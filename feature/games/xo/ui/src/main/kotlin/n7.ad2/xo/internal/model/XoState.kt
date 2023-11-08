package n7.ad2.xo.internal.model

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Immutable
internal data class XoState(
    val deviceIP: String,
    val servers: List<ServerUI>,
    val isGameStarted: Boolean = false,
    val isStartEnabled: Boolean = true,
    val logs: List<String> = emptyList(),
) {

    companion object {
        fun init() = XoState(
            "",
            emptyList(),
        )
    }
}

internal fun MutableStateFlow<XoState>.startGame() = update { it.copy(isGameStarted = true) }

internal fun MutableStateFlow<XoState>.disableStart() = update { it.copy(isStartEnabled = false) }

internal fun MutableStateFlow<XoState>.addLog(log: String) = update { it.copy(logs = it.logs + log) }

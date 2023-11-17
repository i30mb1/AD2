package n7.ad2.xo.internal.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import n7.ad2.app.logger.model.AppLog
import n7.ad2.logger.ui.Logger
import n7.ad2.ui.compose.AppTheme
import n7.ad2.xo.internal.XoState
import n7.ad2.xo.internal.compose.model.ServerUI

@Preview
@Composable
private fun XoScreenPreview() {
    AppTheme {
        XoScreen(
            state = XoState.init().copy(
                deviceIP = "192.168.100.10",
                servers = listOf(ServerUI()),
                logs = listOf(AppLog("Hello")),
            )
        )
    }
}

@Composable
internal fun XoScreen(
    state: XoState,
    modifier: Modifier = Modifier,
    events: (event: XoScreenEvent) -> Unit = { },
) {
    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
    ) {
        Logger(
            logs = state.logs,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
        when {
            state.isGameStarted -> GameScreen(logs = state.messages, events)
            else -> StaringScreen(state, events)
        }
    }
}

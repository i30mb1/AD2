package n7.ad2.xo.internal.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import n7.ad2.ui.compose.AppTheme
import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.model.XoState

@Preview
@Composable
private fun XoScreenPreview() {
    AppTheme {
        XoScreen(
            state = XoState.init().copy(
                deviceIP = "192.168.100.10",
                servers = listOf(ServerUI()),
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
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(modifier = modifier.padding(top = insetsTop, bottom = insetsBottom)) {
        when {
            state.isGameStarted -> GameScreen(logs = state.logs, events)
            else -> StaringScreen(state, events)
        }
    }
}

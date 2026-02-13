package n7.ad2.xo.internal.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.app.logger.model.AppLog
import n7.ad2.feature.games.xo.ui.R
import n7.ad2.ui.compose.AppTheme
import n7.ad2.xo.internal.XoUIState
import n7.ad2.xo.internal.compose.model.ServerUI

@Preview
@Composable
private fun XoScreenPreview() {
    AppTheme {
        XoScreen(
            state = XoUIState(
                deviceIP = "192.168.100.10",
                servers = listOf(ServerUI()),
                logs = listOf(AppLog("Hello")),
            ),
        )
    }
}

@Composable
internal fun XoScreen(state: XoUIState, modifier: Modifier = Modifier, events: (event: XoScreenEvent) -> Unit = { }) {
    Box(
        modifier = modifier
            .safeDrawingPadding(),
    ) {
//        NetworkInfo(state)
//        Logger(
//            logs = state.logs,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .safeDrawingPadding(),
//        )
        when {
            state.isGameStarted -> GameScreen(messages = state.messages, events)
            else -> StaringScreen(state, events)
        }
    }
}

@Composable
private fun BoxScope.NetworkInfo(state: XoUIState) {
    Column(
        modifier = Modifier.Companion
            .align(Alignment.TopEnd)
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
    ) {
        Row(
            modifier = Modifier.align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val icon = if (state.deviceIP.isEmpty()) R.drawable.wifi_off else R.drawable.wifi
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = ImageVector.vectorResource(icon),
                tint = AppTheme.color.textSecondaryColor,
                contentDescription = null,
            )
            Text(
                text = state.deviceIP,
                style = AppTheme.style.body,
                color = AppTheme.color.textSecondaryColor,
            )
        }
        state.server?.let { server ->
            Row(modifier = Modifier.align(Alignment.End)) {
                Text(
                    text = "Host for ",
                    style = AppTheme.style.body,
                    color = AppTheme.color.textSecondaryColor,
                )
                Text(
                    text = server.ip,
                    style = AppTheme.style.body,
                    color = AppTheme.color.textSecondaryColor,
                )
                Text(
                    text = ":${server.port}",
                    style = AppTheme.style.body,
                    color = AppTheme.color.textSecondaryColor,
                )
            }
        }
    }
}

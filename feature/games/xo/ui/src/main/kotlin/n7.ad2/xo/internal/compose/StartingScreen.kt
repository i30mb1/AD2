package n7.ad2.xo.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.feature.games.xo.ui.R
import n7.ad2.ui.compose.AppTheme
import n7.ad2.xo.internal.XoUIState
import n7.ad2.xo.internal.compose.model.ServerUI

@Preview
@Composable
private fun XoScreenPreview() {
    AppTheme {
        StaringScreen(
            XoUIState.init().copy(
                deviceIP = "192.168.100.10",
                servers = listOf(ServerUI()),
            )
        ) { }
    }
}

@Composable
internal fun StaringScreen(
    state: XoUIState,
    event: (event: XoScreenEvent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.align(Alignment.Center),
        ) {
            val name = TextFieldState(state.deviceName)
            val ip = TextFieldState(state.deviceIP)
            EditTextWithButton(
                name,
                "Start",
                state.isStartEnabled,
            ) { event(XoScreenEvent.StartServer(name.text.toString())) }
            EditTextWithButton(
                ip,
                "Connect",
                true,
            ) { event(XoScreenEvent.ConnectToServer(ServerUI(name.text.toString(), ip.text.toString()))) }
            ServerList(state.servers, { server ->
                event(XoScreenEvent.ConnectToServer(server))
            })
        }
    }
}

@Composable
private fun EditTextWithButton(
    state: TextFieldState,
    buttonText: String,
    isEnabled: Boolean,
    onButtonClicked: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(48.dp)
            .clip(AppTheme.shape.medium),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(min = 200.dp)
                .background(AppTheme.color.surface),
            contentAlignment = Alignment.Center,
        ) {
            BasicTextField2(
                state = state,
                lineLimits = TextFieldLineLimits.SingleLine,
                textStyle = LocalTextStyle.current.copy(
                    color = AppTheme.color.textColor,
                    textAlign = TextAlign.End,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )
        }
        Button(
            shape = RoundedCornerShape(0.dp),
            onClick = { onButtonClicked() },
            modifier = Modifier.fillMaxHeight(),
            enabled = isEnabled,
        ) {
            Text(
                text = buttonText,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    }
}


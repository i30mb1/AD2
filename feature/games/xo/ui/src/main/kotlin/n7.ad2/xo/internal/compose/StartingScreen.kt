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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.ui.compose.AppTheme
import n7.ad2.xo.internal.model.ServerUI
import n7.ad2.xo.internal.model.XoState

@Preview
@Composable
private fun XoScreenPreview() {
    AppTheme {
        StaringScreen(
            XoState("192.168.100.10", listOf(ServerUI("192.168.100.11"))), { }
        )
    }
}

@Composable
internal fun StaringScreen(
    state: XoState,
    onClicked: (event: XoScreenEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        var textField by remember(state.deviceIP) { mutableStateOf(state.deviceIP) }
        if (state.deviceIP.isNotBlank()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "device IP:",
                    style = AppTheme.style.body,
                    color = AppTheme.color.textSecondaryColor,
                )
                Text(
                    text = state.deviceIP,
                    style = AppTheme.style.body,
                    color = AppTheme.color.textSecondaryColor,
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Button(
                shape = AppTheme.shape.medium,
                onClick = { onClicked(XoScreenEvent.StartServer) },
                modifier = Modifier.height(48.dp),
                enabled = state.isStartEnabled,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "Start",
                    textAlign = TextAlign.Center,
                )
            }
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
                    BasicTextField(
                        value = textField,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = AppTheme.color.textColor,
                            textAlign = TextAlign.End,
                        ),
                        onValueChange = { textField = it },
                    )
                }
                Button(
                    shape = RoundedCornerShape(0.dp),
                    onClick = { onClicked(XoScreenEvent.ConnectToServer(textField)) },
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    Text(
                        text = "Connect",
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            }
            if (state.servers.isNotEmpty()) {
                ServerList(state.servers, {})
            }
        }
    }
}


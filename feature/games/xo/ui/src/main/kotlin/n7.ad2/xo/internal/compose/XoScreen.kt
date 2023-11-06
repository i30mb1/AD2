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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.ui.compose.AppTheme
import n7.ad2.xo.internal.XoState

@Preview
@Composable
private fun XoScreenPreview() {
    AppTheme {
        XoScreen(
            state = XoState("192.168.100.10", emptyList())
        )
    }
}

@Composable
internal fun XoScreen(
    state: XoState,
    onClicked: (event: XoScreenEvents) -> Unit = { },
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
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
                onClick = { onClicked(XoScreenEvents.StartServer) },
                modifier = Modifier.height(48.dp),
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
                        value = "192.168.10.7",
                        singleLine = true,
                        textStyle = TextStyle(color = AppTheme.color.textColor),
                        onValueChange = { },
                    )
                }
                Button(
                    shape = RoundedCornerShape(0.dp),
                    onClick = { onClicked(XoScreenEvents.ConnectToServer) },
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    Text(
                        text = "Connect",
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            }
            Row(
                modifier = Modifier.padding(4.dp),
            ) {
                Text(
                    text = "Found servers",
                    style = AppTheme.style.body,
                    color = AppTheme.color.textSecondaryColor,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
            ServerList(state.servers)
        }
    }
}

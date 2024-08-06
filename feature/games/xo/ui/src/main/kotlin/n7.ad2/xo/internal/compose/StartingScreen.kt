package n7.ad2.xo.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.InputTransformation
import androidx.compose.foundation.text2.input.TextFieldBuffer
import androidx.compose.foundation.text2.input.TextFieldCharSequence
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.insert
import androidx.compose.foundation.text2.input.rememberTextFieldState
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
import n7.ad2.feature.games.xo.domain.model.SimpleServer
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
                deviceName = "Nothing Phone 2",
                deviceIP = "192.168.100.10",
                servers = listOf(ServerUI()),
                server = SimpleServer("Nothing Phone2", "192.168.100.10", 45646)
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
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateTopPadding()
            ),
    ) {
        if (true) Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Row(
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
                Text(
                    text = ":${server.port}",
                    style = AppTheme.style.body,
                    color = AppTheme.color.textSecondaryColor,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
        if (state.deviceName.isNotEmpty() && state.deviceIP.isNotEmpty()) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(modifier = Modifier.fillMaxHeight(0.3f))
                val name = rememberTextFieldState(state.deviceName)
                val ip = rememberTextFieldState(state.deviceIP)
                EditTextWithButton(
                    name,
                    "Start",
                    state.isButtonStartEnabled,
                ) { event(XoScreenEvent.StartServer(name.text.toString())) }
                if (false) EditTextWithButton(
                    ip,
                    "Connect",
                    true,
                ) {
                    event(
                        XoScreenEvent.ConnectToServer(
                            ServerUI(
                                name.text.toString(),
                                ip.text.toString().substringBefore(":"),
                                ip.text.toString().substringAfter(":"),
                            )
                        )
                    )
                }
                ServerList(state.servers, { server ->
                    event(XoScreenEvent.ConnectToServer(server))
                })
            }
        }
    }
}

@Composable
private fun EditTextWithButton(
    state: TextFieldState,
    buttonText: String,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onButtonClicked: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
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
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
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

object DigitsOnlyTransformation : InputTransformation {
    override val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

    override fun transformInput(
        originalValue: TextFieldCharSequence,
        valueWithChanges: TextFieldBuffer,
    ) {
        if (valueWithChanges.length > 15) {
            valueWithChanges.revertAllChanges()
            return
        }
        valueWithChanges.asCharSequence().forEachIndexed { index, c ->
            if (index == 0) return@forEachIndexed
            if (index % 3 == 0 && c != '.') valueWithChanges.insert(index, ".")
        }
    }
}

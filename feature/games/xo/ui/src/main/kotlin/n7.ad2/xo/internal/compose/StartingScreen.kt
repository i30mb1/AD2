package n7.ad2.xo.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.feature.games.xo.domain.model.SimpleServer
import n7.ad2.ui.compose.AppTheme
import n7.ad2.xo.internal.XoUIState
import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.model.SocketType

@Preview
@Composable
private fun XoScreenPreview() {
    AppTheme {
        StaringScreen(
            XoUIState(
                deviceName = "Nothing Phone 2",
                deviceIP = "192.168.100.10",
                servers = listOf(ServerUI()),
                server = SimpleServer("Nothing Phone2", "192.168.100.10", 45646),
                selectedSocketType = SocketType.RAW
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SocketTypeSelector(
                selectedSocketType = state.selectedSocketType,
                onSocketTypeSelected = { event(XoScreenEvent.SelectSocketType(it)) }
            )
            Box(modifier = Modifier.fillMaxHeight(0.2f))
            val name = rememberTextFieldState(state.deviceName)
            val ip = rememberTextFieldState(state.deviceIP)

            LaunchedEffect(state) {
                if (state.deviceIP != ip.text.toString()) {
                    ip.edit { replace(0, ip.text.length, state.deviceIP) }
                }
                if (state.deviceName != name.text.toString()) {
                    name.edit { replace(0, name.text.length, state.deviceName) }
                }
            }
            EditTextWithButton(
                name,
                "Start",
                state.isButtonStartEnabled,
            ) { event(XoScreenEvent.StartServer(name.text.toString())) }
            if (true) EditTextWithButton(
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

@Composable
internal fun EditTextWithButton(
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

@Composable
internal fun SocketTypeSelector(
    selectedSocketType: SocketType,
    onSocketTypeSelected: (SocketType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = AppTheme.color.surface,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp)
        ) {
            SocketType.entries.forEach { socketType ->
                val isSelected = selectedSocketType == socketType
                Box(
                    modifier = Modifier
                        .clickable { onSocketTypeSelected(socketType) }
                        .background(
                            color = if (isSelected) AppTheme.color.primary else AppTheme.color.surface,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = socketType.name,
                        color = if (isSelected) AppTheme.color.surface else AppTheme.color.textColor.copy(alpha = 0.6f)
                    )
                }
            }
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

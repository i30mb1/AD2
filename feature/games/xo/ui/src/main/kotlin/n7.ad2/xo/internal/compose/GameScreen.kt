package n7.ad2.xo.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.Bold
import n7.ad2.xo.internal.game.Message

@Preview
@Composable
private fun GameScreenPreview() {
    AppTheme {
        GameScreen(listOf(Message.Server("Hello"), Message.Client("Hello")), { })
    }
}

@Composable
internal fun GameScreen(
    messages: List<Message>,
    event: (event: XoScreenEvent) -> Unit,
    isHost: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = WindowInsets.systemBars.asPaddingValues(),
        ) {
            items(messages.size) { index ->
                val log = messages[index]
                val isClient = log is Message.Client
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = log.message,
                        style = AppTheme.style.body.Bold,
                        color = AppTheme.color.textColor,
                        modifier = Modifier
                            .padding(4.dp)
                            .align(if (isClient) Alignment.Start else Alignment.End)
                            .clip(AppTheme.shape.medium)
                            .background(if (isClient) AppTheme.color.surface else AppTheme.color.primary)
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                    )
                }
            }
        }

        Button(
            onClick = { event(XoScreenEvent.SendPing) },
            modifier = Modifier.safeContentPadding(),
        ) {
            val text = if (isHost) "Ping" else "Pong"
            Text(text = text)
        }
    }
}

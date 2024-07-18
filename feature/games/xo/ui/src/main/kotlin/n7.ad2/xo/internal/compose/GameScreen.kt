package n7.ad2.xo.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
    modifier: Modifier = Modifier,
) {
    val topDensity: Dp = with(LocalDensity.current) { WindowInsets.systemBars.getTop(this).toDp() }
    Column {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .weight(1f),
        ) {
            items(messages.size) { index ->
                val log = messages[index]
                val isClient = log is Message.Client
                val paddingTop = if (index == 0) topDensity else 0.dp
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingTop)) {
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
        Row {
            TextButton(onClick = { event(XoScreenEvent.SendPing) }) {
                Text(text = "Ping")
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { event(XoScreenEvent.SendPong) }) {
                Text(text = "Pong")
            }
        }

    }
}

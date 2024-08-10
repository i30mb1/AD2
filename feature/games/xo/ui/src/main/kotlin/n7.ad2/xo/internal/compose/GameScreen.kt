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
import n7.ad2.feature.games.xo.domain.internal.server2.data.Message
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.Bold

@Preview
@Composable
private fun GameScreenPreview() {
    AppTheme {
        GameScreen(
            listOf(Message.Other("Hello"), Message.Me("Hello")), { })
    }
}

@Composable
internal fun GameScreen(
    messages: List<Message>,
    event: (event: XoScreenEvent) -> Unit,
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
                val message = messages[index]
                val isMe = message is Message.Me
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = message.message,
                        style = AppTheme.style.body.Bold,
                        color = AppTheme.color.textColor,
                        modifier = Modifier
                            .padding(4.dp)
                            .align(if (isMe) Alignment.End else Alignment.Start)
                            .clip(AppTheme.shape.medium)
                            .background(if (isMe) AppTheme.color.primary else AppTheme.color.surface)
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                    )
                }
            }
        }

        Button(
            onClick = { event(XoScreenEvent.SendPing) },
            modifier = Modifier.safeContentPadding(),
        ) {
            Text(text = "Ping")
        }
    }
}

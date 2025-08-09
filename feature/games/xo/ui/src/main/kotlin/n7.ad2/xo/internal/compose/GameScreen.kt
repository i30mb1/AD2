package n7.ad2.xo.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.feature.games.xo.domain.internal.server.data.Message
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    when (message) {
                        is Message.Info -> Text(
                            text = message.text,
                            style = AppTheme.style.info,
                            color = AppTheme.color.textColor,
                            modifier = Modifier
                                .padding(4.dp)
                                .align(Alignment.CenterHorizontally)
                                .clip(AppTheme.shape.small)
                                .background(AppTheme.color.surface)
                                .padding(4.dp),
                        )

                        is Message.Me -> Text(
                            text = message.text,
                            style = AppTheme.style.body.Bold,
                            color = AppTheme.color.textColor,
                            modifier = Modifier
                                .padding(4.dp)
                                .align(Alignment.End)
                                .clip(AppTheme.shape.medium)
                                .background(AppTheme.color.primary)
                                .padding(8.dp),
                        )

                        is Message.Other -> Text(
                            text = message.text,
                            style = AppTheme.style.body.Bold,
                            color = AppTheme.color.textColor,
                            modifier = Modifier
                                .padding(4.dp)
                                .align(Alignment.Start)
                                .clip(AppTheme.shape.medium)
                                .background(AppTheme.color.surface)
                                .padding(8.dp),
                        )
                    }
                }
            }
        }
        val message = rememberTextFieldState("")
        EditTextWithButton(
            message,
            "Send",
            true,
        ) { event(XoScreenEvent.SendMessage(message.text.toString())) }
    }
}

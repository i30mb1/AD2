package n7.ad2.logger.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.app.logger.model.AppLog
import n7.ad2.ui.compose.AppTheme

@Preview
@Composable
private fun LoggerImpl() {
    AppTheme {
        Logger(
            listOf(
                AppLog("Hello"),
                AppLog("Hello 2"),
            )
        )
    }
}

@Composable
fun Logger(
    logs: List<AppLog>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(end = 8.dp),
        reverseLayout = false,
        horizontalAlignment = Alignment.End,
    ) {
        items(logs.size) { index ->
            val log = logs[index]
            Text(
                text = log.message,
                style = AppTheme.style.body,
                color = AppTheme.color.textColor,
                minLines = 1,
            )
        }
    }
}

package n7.ad2.xo.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.Bold

@Preview
@Composable
private fun GameScreenPreview() {
    AppTheme {
        GameScreen(listOf("Hello", "Hello Sam"))
    }
}

@Composable
internal fun GameScreen(
    logs: List<String>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(logs.size) { index ->
            val log = logs[index]
            Text(
                text = log,
                style = AppTheme.style.body.Bold,
                color = AppTheme.color.textColor,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(AppTheme.shape.medium)
                    .background(AppTheme.color.surface)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
            )
        }
    }
}

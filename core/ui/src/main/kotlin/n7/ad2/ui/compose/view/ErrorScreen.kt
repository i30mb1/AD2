package n7.ad2.ui.compose.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.ui.R
import n7.ad2.ui.compose.AppTheme

@Preview
@Composable
fun ErrorScreen(
    error: Throwable? = null,
    onRetryClicked: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = error?.message ?: "???",
            style = AppTheme.style.H5,
            color = AppTheme.color.textColor,
            modifier = Modifier
                .padding(32.dp)
        )
        Button(onClick = onRetryClicked) {
            Text(text = stringResource(id = R.string.ui_retry))
        }
    }

}
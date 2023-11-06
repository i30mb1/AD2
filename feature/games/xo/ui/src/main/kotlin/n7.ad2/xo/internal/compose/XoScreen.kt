package n7.ad2.xo.internal.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.ui.compose.AppTheme
import n7.ad2.xo.internal.XoViewModel

@Preview
@Composable
private fun XoScreenPreview() {
    AppTheme {
        XoScreen(XoViewModel.State.Data("192.168.10.07"))
    }
}

@Composable
internal fun XoScreen(
    state: XoViewModel.State,
    onClicked: (event: XoScreenEvents) -> Unit = { },
) {
    val state = state as? XoViewModel.State.Data
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = { onClicked(XoScreenEvents.StartServer) },
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "Start",
                    textAlign = TextAlign.Center,
                )
            }
            Text(text = "on ${state?.ip}", color = Color.White)
            Button(
                onClick = { onClicked(XoScreenEvents.ConnectToServer) },
            ) {
                Text(text = "Connect")
            }
        }
    }
}

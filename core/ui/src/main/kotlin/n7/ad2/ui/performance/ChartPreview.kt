package n7.ad2.ui.performance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.ui.compose.AppTheme

@Preview(showBackground = true)
@Composable
private fun ChartPreview() {
    AppTheme {
        val sampleData = ChartState(
            values = listOf(
                ChartState.ChartValue(value = 10, color = Color.Red),
                ChartState.ChartValue(value = 25, color = Color.Blue),
                ChartState.ChartValue(value = 40, color = Color.Green),
                ChartState.ChartValue(value = 30, color = Color.Yellow),
                ChartState.ChartValue(value = 60, color = Color.Cyan),
                ChartState.ChartValue(value = 45, color = Color.Magenta),
                ChartState.ChartValue(value = 70, color = Color.Red),
                ChartState.ChartValue(value = 55, color = Color.Blue),
            ),
            maxValue = 80
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Chart(
                state = sampleData,
                modifier = Modifier.height(200.dp),
                lineColor = Color.Red,
                textColor = Color.Black,
                gridColor = Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChartSimplePreview() {
    AppTheme {
        val simpleData = ChartState(
            values = listOf(
                ChartState.ChartValue(value = 20, color = Color.Blue),
                ChartState.ChartValue(value = 50, color = Color.Blue),
                ChartState.ChartValue(value = 30, color = Color.Blue),
            ),
            maxValue = 60
        )

        Chart(
            state = simpleData,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .height(150.dp)
        )
    }
}

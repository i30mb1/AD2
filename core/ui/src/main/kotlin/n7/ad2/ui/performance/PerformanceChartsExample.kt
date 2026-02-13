package n7.ad2.ui.performance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.flowOf
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.ui.compose.AppTheme

/**
 * Example showing how to use the new PerformanceCharts composable
 */
@Preview(showBackground = true)
@Composable
private fun PerformanceChartsExample() {
    AppTheme {
        // Example usage data
        val sampleUsageData = listOf(
            ResourceUsage(
                cpu = ResourceUsage.Info(50, ResourceUsage.Status.GOOD),
                ram = ResourceUsage.Info(70, ResourceUsage.Status.FAIR),
                fps = ResourceUsage.Info(60, ResourceUsage.Status.VERY_GOOD),
            ),
            ResourceUsage(
                cpu = ResourceUsage.Info(30, ResourceUsage.Status.VERY_GOOD),
                ram = ResourceUsage.Info(65, ResourceUsage.Status.GOOD),
                fps = ResourceUsage.Info(58, ResourceUsage.Status.GOOD),
            ),
            ResourceUsage(
                cpu = ResourceUsage.Info(80, ResourceUsage.Status.POOR),
                ram = ResourceUsage.Info(90, ResourceUsage.Status.VERY_BAD),
                fps = ResourceUsage.Info(60, ResourceUsage.Status.EXCELLENT),
            ),
        )

        val mockDispatcher = object : DispatchersProvider() {
            override val Main = kotlinx.coroutines.Dispatchers.Main
            override val IO = kotlinx.coroutines.Dispatchers.IO
            override val Default = kotlinx.coroutines.Dispatchers.Default
        }

        val chartsOwner = remember { ChartsOwner(mockDispatcher) }
        val usageFlow = remember { flowOf(sampleUsageData) }

        Column {
            PerformanceCharts(
                usageFlow = usageFlow,
                chartsOwner = chartsOwner,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}

/**
 * Simplified example showing manual Chart usage
 */
@Preview(showBackground = true)
@Composable
private fun ManualChartsExample() {
    AppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // CPU Chart
            Chart(
                state = ChartState(
                    values = listOf(
                        ChartState.ChartValue(30, Color.Green),
                        ChartState.ChartValue(50, Color.Yellow),
                        ChartState.ChartValue(80, Color.Red),
                        ChartState.ChartValue(40, Color.Green),
                    ),
                    maxValue = 100,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                lineColor = Color.Red,
                textColor = Color.Black,
                gridColor = Color.Gray.copy(alpha = 0.5f),
            )
        }
    }
}

package n7.ad2.ui.performance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.coroutines.DispatchersProvider

class ChartsOwner(private val dispatcher: DispatchersProvider) {
    private val mapper = LineChartMapper(dispatcher)

    suspend fun mapData(list: List<ResourceUsage>): List<ChartState> = mapper.map(list)
}

/**
 * Compose version of performance charts
 */
@Composable
fun PerformanceCharts(usageFlow: Flow<List<ResourceUsage>>, chartsOwner: ChartsOwner, modifier: Modifier = Modifier) {
    val chartStatesFlow = usageFlow.map { chartsOwner.mapData(it) }
    val chartStates by chartStatesFlow.collectAsState(initial = emptyList())

    if (chartStates.size >= 3) {
        val (cpuState, ramState, fpsState) = chartStates

        Column(modifier = modifier) {
            // CPU Chart
            Text(
                text = "CPU",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Chart(
                state = cpuState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 8.dp),
                lineColor = Color.Red,
                textColor = Color.White,
                gridColor = Color.Gray.copy(alpha = 0.3f),
            )

            // RAM Chart
            Text(
                text = "RAM",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Chart(
                state = ramState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 8.dp),
                lineColor = Color.Blue,
                textColor = Color.White,
                gridColor = Color.Gray.copy(alpha = 0.3f),
            )

            // FPS Chart
            Text(
                text = "FPS",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Chart(
                state = fpsState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 8.dp),
                lineColor = Color.Green,
                textColor = Color.White,
                gridColor = Color.Gray.copy(alpha = 0.3f),
            )
        }
    }
}

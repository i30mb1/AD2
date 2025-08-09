package n7.ad2.ui.performance

import kotlin.math.max
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.ui.frameCounter.toComposeColor

internal class LineChartMapper(
    private val dispatcher: DispatchersProvider,
) {

    suspend fun map(list: List<ResourceUsage>): List<ChartState> {
        return withContext(dispatcher.Default) {
            val cpu: MutableList<ChartState.ChartValue> = ArrayList(list.size)
            val ram: MutableList<ChartState.ChartValue> = ArrayList(list.size)
            val fps: MutableList<ChartState.ChartValue> = ArrayList(list.size)

            var cpuMax = 0
            var ramMax = 0
            var fpsMax = 0

            for (usage in list) {
                cpuMax = max(cpuMax, usage.cpu.value)
                ramMax = max(ramMax, usage.ram.value)
                fpsMax = max(fpsMax, usage.fps.value)

                cpu += ChartState.ChartValue(
                    usage.cpu.value,
                    usage.cpu.status.toComposeColor(),
                )

                ram += ChartState.ChartValue(
                    usage.ram.value,
                    usage.ram.status.toComposeColor(),
                )

                fps += ChartState.ChartValue(
                    usage.fps.value,
                    usage.fps.status.toComposeColor(),
                )
            }

            listOf(
                ChartState(cpu, cpuMax),
                ChartState(ram, ramMax),
                ChartState(fps, fpsMax),
            )
        }
    }
}

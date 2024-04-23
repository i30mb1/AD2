package n7.ad2.ui.performance

import kotlin.math.max
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.ui.frameCounter.toColor

internal class LineChartMapper(
    private val dispatcher: DispatchersProvider,
) {

    suspend fun map(list: List<ResourceUsage>): List<ChartView.State> {
        return withContext(dispatcher.Default) {
            val cpu: MutableList<ChartView.State.ColoredValue> = ArrayList(list.size)
            val ram: MutableList<ChartView.State.ColoredValue> = ArrayList(list.size)
            val fps: MutableList<ChartView.State.ColoredValue> = ArrayList(list.size)

            var cpuMax = 0
            var ramMax = 0
            var fpsMax = 0

            for (usage in list) {
                cpuMax = max(cpuMax, usage.cpu.value)
                ramMax = max(ramMax, usage.ram.value)
                fpsMax = max(fpsMax, usage.fps.value)

                cpu += ChartView.State.ColoredValue(
                    usage.cpu.value,
                    usage.cpu.status.toColor(),
                )

                ram += ChartView.State.ColoredValue(
                    usage.ram.value,
                    usage.ram.status.toColor(),
                )

                fps += ChartView.State.ColoredValue(
                    usage.fps.value,
                    usage.fps.status.toColor(),
                )
            }

            listOf(
                ChartView.State(cpu, cpuMax),
                ChartView.State(ram, ramMax),
                ChartView.State(fps, fpsMax),
            )
        }
    }
}
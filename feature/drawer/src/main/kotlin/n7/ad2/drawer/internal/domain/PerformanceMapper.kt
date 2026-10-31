package n7.ad2.drawer.internal.domain

import n7.ad2.core.ui.performance.ResourceUsage
import n7.ad2.drawer.internal.domain.vo.VOPerformance

/** Turns the poller history into the three meters of the device tile. */
internal object PerformanceMapper {

    /** The poller samples every 300 ms, so the graph shows about the last 9 seconds. */
    const val SAMPLES = 30
    private const val CPU_MAX = 100

    fun map(history: List<ResourceUsage>): List<VOPerformance> {
        if (history.isEmpty()) return emptyList()
        val window = history.takeLast(SAMPLES)
        return listOf(
            meter(VOPerformance.Kind.CPU, window.map { it.cpu }, max = CPU_MAX),
            meter(VOPerformance.Kind.RAM, window.map { it.ram }, max = null),
            meter(VOPerformance.Kind.FPS, window.map { it.fps }, max = null),
        )
    }

    /** [max] null scales the graph to the highest sample of the window. */
    private fun meter(kind: VOPerformance.Kind, infos: List<ResourceUsage.Info>, max: Int?): VOPerformance {
        val scale = (max ?: infos.maxOf { it.value }).coerceAtLeast(1).toFloat()
        val current = infos.last()
        return VOPerformance(kind, current.value, current.status, infos.map { (it.value / scale).coerceIn(0f, 1f) })
    }
}
